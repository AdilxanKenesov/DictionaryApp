#!/usr/bin/env python3
"""
Ilova uchun asset DB yig'adi: app/src/main/assets/database/dictionary.db

Manbalar:
  dictionary.db      -> words (EN -> UZ, transkripsiya, countable)
  eng_dictionary.db  -> definition jadvali (Webster inglizcha izohlari)

Jadval sxemasi Room entity'lari bilan bir xil bo'lishi SHART
(WordEntity, DefinitionEntity). Entity o'zgarsa bu yerni ham yangilang
va DictionaryDatabase versiyasini oshiring (DB_VERSION bilan birga).

Ishga tushirish:
  python3 tools/build_dictionary_db.py ~/Downloads/"Telegram Desktop"
"""
import os
import re
import sqlite3
import sys

DB_VERSION = 1

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT = os.path.join(ROOT, "app", "src", "main", "assets", "database", "dictionary.db")

SCHEMA = [
    "CREATE TABLE IF NOT EXISTS `words` (`id` INTEGER NOT NULL, `english` TEXT NOT NULL COLLATE NOCASE, "
    "`type` TEXT NOT NULL, `transcript` TEXT NOT NULL, `uzbek` TEXT NOT NULL COLLATE NOCASE, "
    "`countable` TEXT, PRIMARY KEY(`id`))",
    "CREATE INDEX IF NOT EXISTS `index_words_english` ON `words` (`english`)",
    "CREATE INDEX IF NOT EXISTS `index_words_uzbek` ON `words` (`uzbek`)",
    "CREATE TABLE IF NOT EXISTS `definitions` (`id` INTEGER NOT NULL, `word` TEXT NOT NULL COLLATE NOCASE, "
    "`type` TEXT NOT NULL, `description` TEXT NOT NULL, PRIMARY KEY(`id`))",
    "CREATE INDEX IF NOT EXISTS `index_definitions_word` ON `definitions` (`word`)",
]

# Webster qisqartmalari -> ilovadagi turkum nomlari
POS = {
    "n.": "noun", "n. pl.": "noun", "pl.": "noun",
    "v. t.": "verb", "v. i.": "verb", "v.": "verb", "v. t. & i.": "verb", "v. i. & t.": "verb",
    "a.": "adj", "adj.": "adj",
    "adv.": "adv", "prep.": "prep", "pron.": "pron", "conj.": "conj", "interj.": "excl",
    "p. p.": "verb", "imp.": "verb", "p. pr.": "verb", "p. pr. & vb. n.": "verb",
    "imp. & p. p.": "verb", "p. a.": "adj", "superl.": "adj", "compar.": "adj",
    "p. p. & a.": "adj", "a. & n.": "adj", "n. & v.": "noun",
}


def clean(text):
    text = re.sub(r"\s+", " ", (text or "")).strip()
    return text.strip(" ,;")


def countable(raw):
    raw = re.sub(r"[\[\]\s]", "", raw or "").upper()
    if not raw:
        return None
    raw = raw.replace("PLURALNOUN", "PL").replace("PLURAL", "PL").replace("OR", "/")
    return "/".join(dict.fromkeys(raw.split("/")))


def pos(raw):
    raw = clean((raw or "").strip("()"))
    # Ba'zi yozuvlarda oxirgi nuqta tushib qolgan: "superl" -> "superl."
    return POS.get(raw, POS.get(raw + ".", raw))


def main(src_dir):
    words_src = sqlite3.connect(os.path.join(src_dir, "dictionary.db"))
    defs_src = sqlite3.connect(os.path.join(src_dir, "eng_dictionary.db"))

    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    if os.path.exists(OUT):
        os.remove(OUT)
    out = sqlite3.connect(OUT)
    for sql in SCHEMA:
        out.execute(sql)

    seen = set()
    words = []
    for wid, english, wtype, transcript, uzbek, cnt in words_src.execute(
        "SELECT id, english, type, transcript, uzbek, countable FROM dictionary ORDER BY id"
    ):
        english, uzbek = clean(english), clean(uzbek)
        if not english or not uzbek:
            continue
        key = (english.lower(), clean(wtype), uzbek.lower())
        if key in seen:
            continue
        seen.add(key)
        words.append((wid, english, clean(wtype), clean(transcript), uzbek, countable(cnt)))
    out.executemany("INSERT INTO words VALUES (?, ?, ?, ?, ?, ?)", words)

    vocabulary = {w[1].lower() for w in words}
    defs = []
    for word, dtype, desc in defs_src.execute("SELECT Word, Type, Description FROM definition ORDER BY ID"):
        word = clean(word).lower()
        desc = clean(desc)
        if word in vocabulary and desc:
            defs.append((len(defs) + 1, word, pos(dtype), desc))
    out.executemany("INSERT INTO definitions VALUES (?, ?, ?, ?)", defs)

    out.execute(f"PRAGMA user_version = {DB_VERSION}")
    out.commit()
    out.execute("VACUUM")
    out.close()

    print(f"words: {len(words)}, definitions: {len(defs)}, size: {os.path.getsize(OUT) / 1024 / 1024:.1f} MB")
    print(f"-> {OUT}")


if __name__ == "__main__":
    main(sys.argv[1] if len(sys.argv) > 1 else os.path.expanduser("~/Downloads/Telegram Desktop"))

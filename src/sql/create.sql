-- name: create-terms!
-- Tracks the syllable counts for each term
CREATE TABLE "terms" ("id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "term" varchar(255), "syllables" integer, "cmu_dict" boolean DEFAULT 'f' NOT NULL, "syllable_count_varies" boolean DEFAULT 'f' NOT NULL);

-- name: create-terms-index-1!
-- An index on the terms table
CREATE UNIQUE INDEX "index_terms_on_term" ON "terms" ("term");

-- name: create-term-misses!
-- A table for tracking misses against the terms table. We can use this to find key terms to add later
CREATE TABLE "term_misses" ("id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "term" varchar(255), "miss_count" integer);

-- name: create-term-misses-index-1!
-- An index of term misses by term
CREATE UNIQUE INDEX "index_term_misses_on_term" ON "term_misses" ("term");

-- name: create-term-misses-index-2!
-- An index of term misses by count (for sorting)
CREATE INDEX "index_term_misses_on_miss_count" ON "term_misses" ("miss_count");

-- name: create-sources!
-- Used for tracking info about when each source was pulled
CREATE TABLE "sources" ("id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "name" varchar(255), "url" varchar(255), "checked_at" datetime);

-- name: create-articles!
-- Used for tracking when individual articles were fetched
CREATE TABLE "articles" ("id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "source_id" integer, "guid" varchar(255), "short_url" varchar(255), "created_at" datetime, "title" varchar(255), "sensitive" boolean DEFAULT 'f' NOT NULL, "published_at" datetime, "summary" text, "sensitive_matches" varchar(255), "perilous_matches" varchar(255), "perilous" boolean DEFAULT 'f' NOT NULL, "subjects" varchar(1024));

-- name: create-articles-index-1!
-- Indexes the GUID on the articles table
CREATE UNIQUE INDEX "index_articles_on_guid" ON "articles" ("guid");

-- name: create-articles-index-2!
-- Indexes the source_id on the articles table
CREATE UNIQUE INDEX "index_articles_on_source_id" ON "articles" ("source_id");

-- name: create-haikus!
-- Create the haikus table
CREATE TABLE "haikus" ("id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "line0" varchar(255), "line1" varchar(255), "line2" varchar(255), "article_id" integer, "source_id" integer, "text_hash" varchar(255), "debug_tweet_id" varchar(255), "created_at" datetime, "awkward" boolean DEFAULT 'f' NOT NULL, "sensitive" boolean DEFAULT 'f' NOT NULL, "meter" varchar(32), "sensitive_matches" varchar(255), "awkward_matches" varchar(255));

-- name: create-haikus-index-1!
-- Index haikus on the article_id
CREATE INDEX "index_haikus_on_article_id" ON "haikus" ("article_id");

-- name: create-haikus-index-2!
-- Index the haikus on the hash
CREATE UNIQUE INDEX "index_haikus_on_hash" ON "haikus" ("text_hash");

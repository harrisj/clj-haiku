-- name: db-add-term!
-- Add a term to the DB
INSERT OR IGNORE INTO terms(term, syllables, cmu_dict, syllable_count_varies) VALUES (:term, :syl_count, :cmu_dict, 0);

-- name: db-set-varies-for-term!
-- Set syllable_count_varies for terms that don't match
UPDATE terms SET syllable_count_varies = 1 WHERE term = :term AND syllables != :syllables

-- name: db-remove-term-from-misses!
-- Delete from the term misses table if we add it
DELETE FROM term_misses WHERE term = :term

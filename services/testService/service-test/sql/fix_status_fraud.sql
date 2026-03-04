-- Erreur "Data truncated for column 'status'" : la colonne n'accepte pas la valeur FRAUD.
-- Exécuter dans MySQL (base freelancify) pour autoriser le statut FRAUD.

-- Si la colonne est en ENUM, la passer en VARCHAR pour accepter toutes les valeurs du Java enum.
ALTER TABLE affectation_test MODIFY COLUMN status VARCHAR(50) NOT NULL;

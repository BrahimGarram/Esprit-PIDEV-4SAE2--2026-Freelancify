package com.example.servicetest.entity;

/**
 * Statut d'une affectation de test (freelancer).
 * Nouveaux : IN_PROGRESS, COMPLETED, ABANDONED.
 * Anciennes valeurs conservées pour compatibilité avec les données existantes en base.
 */
public enum Status {
    /** Test en cours ou non soumis. */
    IN_PROGRESS,
    /** Freelancer a soumis le test (terminé). */
    COMPLETED,
    /** Freelancer a quitté la page sans soumettre. */
    ABANDONED,
    /** Fraude : changement d'onglet, aucun visage détecté, ou visage ne correspond pas à la photo de profil. */
    FRAUD,
    /** @deprecated Ancienne valeur, équivalent à IN_PROGRESS */
    PENDING,
    /** @deprecated Ancienne valeur, équivalent à IN_PROGRESS */
    ACTIVE,
    /** @deprecated Ancienne valeur, équivalent à COMPLETED */
    INACTIVE,
    /** @deprecated Ancienne valeur */
    DELETED
}

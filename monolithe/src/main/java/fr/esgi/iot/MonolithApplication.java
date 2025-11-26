package fr.esgi.iot;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.jboss.logging.Logger;

/**
 * Classe principale de l'application monolithique IoT.
 * 
 * Cette application monolithique combine :
 * - La réception de données capteurs (via MessageBrokerSimulator)
 * - Le stockage en "base de données" (mémoire)
 * - L'exposition d'APIs REST pour la lecture et les commandes
 * 
 * PROBLÈMES de cette architecture monolithique :
 * 1. Tout est couplé dans une seule application
 * 2. Impossible de scaler indépendamment chaque fonctionnalité
 * 3. Déploiement monolithique (tout ou rien)
 * 4. Une erreur peut faire tomber toute l'application
 * 5. Difficile à maintenir et faire évoluer
 * 
 * OBJECTIF du TP : Migrer vers une architecture microservices pour :
 * - Découpler les responsabilités
 * - Permettre le scaling indépendant
 * - Améliorer la résilience
 * - Faciliter la maintenance
 */
@QuarkusMain
public class MonolithApplication implements QuarkusApplication {

    private static final Logger LOG = Logger.getLogger(MonolithApplication.class);

    public static void main(String[] args) {
        LOG.info("🚀 Démarrage de l'application monolithique IoT...");
        Quarkus.run(MonolithApplication.class, args);
    }

    @Override
    public int run(String... args) {
        LOG.info("========================================");
        LOG.info("   APPLICATION MONOLITHIQUE IOT       ");
        LOG.info("========================================");
        LOG.info("");
        LOG.info("✅ Application démarrée avec succès !");
        LOG.info("");
        LOG.info("📡 Endpoints disponibles :");
        LOG.info("  - GET  http://localhost:8080/api/sensors");
        LOG.info("  - GET  http://localhost:8080/api/sensors/{id}");
        LOG.info("  - GET  http://localhost:8080/api/sensors/{id}/data");
        LOG.info("  - POST http://localhost:8080/api/commands");
        LOG.info("");
        LOG.info("📨 Le simulateur de broker génère des données toutes les 10s");
        LOG.info("");
        LOG.info("⚠️  PROBLÈMES de cette architecture :");
        LOG.info("  - Tout est couplé dans une seule application");
        LOG.info("  - API de commande SYNCHRONE et BLOQUANTE");
        LOG.info("  - Impossible de scaler indépendamment");
        LOG.info("  - Déploiement monolithique");
        LOG.info("");
        LOG.info("🎯 VOTRE MISSION : Migrer vers microservices !");
        LOG.info("========================================");

        Quarkus.waitForExit();
        return 0;
    }
}

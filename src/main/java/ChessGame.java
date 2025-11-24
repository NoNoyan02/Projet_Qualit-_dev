import com.chess.configuration.AppConfig;
import com.chess.entrypoints.console.ConsoleController;
import com.chess.entrypoints.gui.GuiController;

/**
 * Classe principale pour lancer le jeu d'échecs.
 */
public class ChessGame {

    public static void main(String[] args) {
        // Configuration de l'application (injection de dépendances)
        AppConfig config = new AppConfig();

        try {
            // Initialisation
            config.initialize();

            // Choix de l'interface
            if (args.length > 0 && args[0].equals("--gui")) {
                launchGUI(config);
            } else if (args.length > 0 && args[0].equals("--web")) {
                launchWeb(config);
            } else {
                launchConsole(config);
            }

        } catch (Exception e) {
            System.err.println("Erreur fatale: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Nettoyage
            config.shutdown();
        }
    }

    /**
     * Lance l'interface console.
     */
    private static void launchConsole(AppConfig config) {
        ConsoleController controller = new ConsoleController(
                config.getMovePieceUseCase(),
                config.getGetBestMoveUseCase()
        );

        controller.startNewGame();
        controller.close();
    }

    /**
     * Lance l'interface graphique (à implémenter).
     */
    private static void launchGUI(AppConfig config) {
        // Création du contrôleur GUI
        GuiController guiController = new GuiController(config);

        // Démarrage de l'application Swing
        guiController.start();
    }

    /**
     * Lance l'interface web (à implémenter).
     */
    private static void launchWeb(AppConfig config) {
        System.out.println("Interface web non implémentée.");
        System.out.println("Lancement de l'interface console à la place...");
        launchConsole(config);
    }
}
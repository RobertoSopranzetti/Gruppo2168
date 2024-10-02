package db_lab.data;

public final class Queries {

        public static final String USER_SIGN_UP = """
                        INSERT INTO UTENTE (Username, Password, Nome, Cognome, Email)
                        VALUES (?, ?, ?, ?, ?);
                        """;

        public static final String ADMIN_SIGN_UP = """
                        INSERT INTO ADMIN (Username, Password, Nome, Cognome, Email)
                        VALUES (?, ?, ?, ?, ?);
                        """;

        public static final String CREATION_INSERT = """
                        INSERT INTO CREAZIONE (dataCreazione, Nome, Descrizione,
                        Forza, Destrezza, Costituzione, Intelligenza, Saggezza, Carisma, IDraccolta)
                        VALUES (CURDATE(), ?, ?, ?, ?, ?, ?, ?, ?, ?);
                        """;

        public static final String CHARACTER_CREATION = """
                        INSERT INTO PERSONAGGIO (IDcreazione, Classe, Razza, Livello)
                        VALUES (?, ?, ?, ?);
                        """;

        public static final String MONSTER_CREATION = """
                        INSERT INTO MOSTRO (IDcreazione, Taglia, Difficoltà, Tipo)
                        VALUES (?, ?, ?, ?);
                        """;

        public static final String PUBLISH_CREATION = """
                        INSERT INTO INSERZIONE (dataPubblicazione, IDcreazione, Username)
                        VALUES (CURDATE(), ?, ?);
                        """;

        public static final String CREATE_COLLECTION = """
                        INSERT INTO RACCOLTA (dataCreazone, Nome, Privata, Username)
                        VALUES (CURDATE(), ?, ?, ?);
                        """;

        public static final String SELECT_COLLECTIONS_BY_USERNAME = """
                        SELECT IDraccolta FROM RACCOLTA WHERE Username = ?;
                        """;

        public static final String DOWNLOAD_CREATION = """
                        INSERT INTO Download (IDinserzione, Username, dataDownload)
                        VALUES (?, ?, CURDATE());
                        """;

        public static final String UPDATE_TOTAL_DOWNLOADS = """
                        UPDATE INSERZIONE
                        SET NumeroDownload = NumeroDownload + 1
                        WHERE IDinserzione = ?;
                        """;

        public static final String VOTE_CREATION = """
                        INSERT INTO Votazione (IDinserzione, Username, Tipo)
                        VALUES (?, ?, ?);  -- Tipo TRUE per upvote, FALSE per downvote
                        """;

        public static final String UPDATE_TOTAL_VOTES = """
                        UPDATE UTENTE u
                        JOIN INSERZIONE i ON u.Username = i.Username
                        SET
                        u.UpvoteTotali = u.UpvoteTotali + CASE WHEN ? = TRUE THEN 1 ELSE 0 END,
                        u.DownvoteTotali = u.DownvoteTotali + CASE WHEN ? = FALSE THEN 1 ELSE 0 END
                        WHERE i.IDinserzione = ?;
                        """;

        public static final String COMMENT_CREATION = """
                        INSERT INTO COMMENTO (dataPubblicazione, Contenuto, IDinserzione, Username)
                        VALUES (CURDATE(), ?, ?, ?);
                        """;

        public static final String REPORT_INSERTION = """
                        INSERT INTO Segnalazione_Inserzione (IDinserzione, Username, dataSegnalazione, Motivo)
                        VALUES (?, ?, CURDATE(), ?);
                        """;

        public static final String REPORT_COMMENT = """
                        INSERT INTO Segnalazione_Commento (IDcommento, Username, dataSegnalazione, Motivo)
                        VALUES (?, ?, CURDATE(), ?);
                        """;

        public static final String UPDATE_USER_STATUS_TEMPLATE = """
                        UPDATE UTENTE
                        SET Segnalato = TRUE
                        WHERE Username = (SELECT Username FROM %s WHERE %s = ?) AND Segnalato = FALSE;
                        """;

        public static final String DETERMINE_CATEGORY = """
                        SELECT
                                CASE
                                WHEN EXISTS (SELECT 1 FROM PERSONAGGIO p WHERE p.IDcreazione = c.IDcreazione) THEN 'Personaggio'
                                WHEN EXISTS (SELECT 1 FROM MOSTRO m WHERE m.IDcreazione = c.IDcreazione) THEN 'Mostro'
                                ELSE 'Nessuna categoria'
                                END AS Categoria
                        FROM
                                INSERZIONE i
                                JOIN CREAZIONE c ON i.IDcreazione = c.IDcreazione
                        WHERE
                                i.IDinserzione = ?;
                        """;

        public static final String GET_OTHER_CHARACTERS = """
                        SELECT c.*, p.*
                        FROM INSERZIONE i
                        JOIN CREAZIONE c ON i.IDcreazione = c.IDcreazione
                        JOIN PERSONAGGIO p ON c.IDcreazione = p.IDcreazione
                        WHERE i.IDinserzione <> ?;
                        """;

        public static final String GET_OTHER_MONSTERS = """
                        SELECT c.*, m.*
                        FROM INSERZIONE i
                        JOIN CREAZIONE c ON i.IDcreazione = c.IDcreazione
                        JOIN MOSTRO m ON c.IDcreazione = m.IDcreazione
                        WHERE i.IDinserzione <> ?;
                        """;

        public static final String SHOW_SUBCATEGORY = """
                        SELECT s.Nome AS Sottocategoria, c.*, p.*, m.*
                        FROM CREAZIONE c
                        LEFT JOIN PERSONAGGIO p ON c.IDcreazione = p.IDcreazione
                        LEFT JOIN MOSTRO m ON c.IDcreazione = m.IDcreazione
                        JOIN INSERZIONE i ON c.IDcreazione = i.IDcreazione
                        JOIN Appartenenza a ON i.IDinserzione = a.IDinserzione
                        JOIN SOTTOCATEGORIA s ON a.IDsottocategoria = s.IDsottocategoria
                        WHERE a.IDsottocategoria IN (
                        SELECT a2.IDsottocategoria
                        FROM Appartenenza a2
                        WHERE a2.IDinserzione = ?
                        )
                        GROUP BY c.IDcreazione, s.Nome
                        ORDER BY s.Nome, c.IDcreazione;
                        """;
        public static final String TOP_CREATORS = """
                        SELECT Username, (UpvoteTotali - DownvoteTotali) AS VotiTotali
                        FROM UTENTE
                        ORDER BY VotiTotali DESC
                        LIMIT 10;
                        """;
        public static final String TOP_CREATIONS = """
                        SELECT c.*, p.*, m.*
                        FROM CREAZIONE c
                        LEFT JOIN PERSONAGGIO p ON c.IDcreazione = p.IDcreazione
                        LEFT JOIN MOSTRO m ON c.IDcreazione = m.IDcreazione
                        JOIN INSERZIONE i ON c.IDcreazione = i.IDcreazione
                        WHERE p.IDcreazione IS NOT NULL OR m.IDcreazione IS NOT NULL
                        ORDER BY i.NumeroDownload DESC
                        LIMIT 10;
                        """;
        public static final String ADD_SUBCATEGORY = """
                        INSERT INTO SOTTOCATEGORIA (dataCreazione, Nome, Descrizione, Username)
                        VALUES (CURDATE(), ?, ?, ?);
                        """;
        public static final String ADD_TO_SUBCATEGORY = """
                        INSERT INTO Appartenenza (IDinserzione, IDsottocategoria)
                        VALUES (?, ?);
                        """;
        public static final String SHOW_BAD_USERS = """
                        SELECT Username
                        FROM UTENTE
                        WHERE DownvoteTotali >= 1000;
                        """;
        public static final String SHOW_REPORTED_USERS = """
                        SELECT Username
                        FROM UTENTE
                        WHERE Segnalato = TRUE;
                        """;

        public static final String USER_MODERATION = """
                        INSERT INTO Moderazione (AdminName, Username, Tipo)
                        VALUES (?, ?, ?);
                        """;

        public static final String USER_REPORT_UPDATE = """
                        UPDATE UTENTE
                        SET Segnalato = FALSE
                        WHERE Username = ? AND Segnalato = TRUE;
                        """;
}

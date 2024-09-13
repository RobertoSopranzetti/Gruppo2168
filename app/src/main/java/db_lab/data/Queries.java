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
    public static final String CHARACTER_CREATION = """
            START TRANSACTION;

            INSERT INTO CREAZIONE (dataCreazione, Nome, Descrizione,
                Forza, Destrezza, Costituzione, Intelligenza, Saggezza, Carisma,
                IDraccolta)
            VALUES (CURDATE(), ?, ?, ?, ?, ?, ?, ?, ?, ?);

            SET @IDcreazione = LAST_INSERT_ID();

            INSERT INTO PERSONAGGIO (IDcreazione, Classe, Razza, Livello)
            VALUES (@IDcreazione, ?, ?, ?);
            """;

    public static final String MONSTER_CREATION = """
            START TRANSACTION;

            INSERT INTO CREAZIONE (dataCreazione, Nome, Descrizione,
                Forza, Destrezza, Costituzione, Intelligenza, Saggezza, Carisma,
                IDraccolta)
            VALUES (CURDATE(), ?, ?, ?, ?, ?, ?, ?, ?, ?);

            SET @IDcreazione = LAST_INSERT_ID();

            INSERT INTO MOSTRO (IDcreazione, Taglia, Difficoltà, Tipo)
            VALUES (@IDcreazione, ?, ?, ?);
            """;

    public static final String PUBLISH_CREATION = """
            INSERT INTO INSERZIONE (dataPubblicazione,
                IDcreazione, Username)
            VALUES (CURDATE(), ?, ?);
            """;

    public static final String CREATE_COLLECTION = """
            INSERT INTO RACCOLTA (dataCreazone, Nome, Privata, Username)
            VALUES (CURDATE(), ?, ?, ?);
            """;

    public static final String DOWNLOAD_CREATION = """
            START TRANSACTION;

            INSERT INTO Download (IDinserzione, Username, dataDownload)
            VALUES (?, ?, CURDATE());

            UPDATE INSERZIONE
            SET NumeroDownload = NumeroDownload + 1
            WHERE IDinserzione = ?;

            COMMIT;
            """;

    public static final String VOTE_CREATION = """
            STA
            RT TRANSACTION;

            INSERT INTO Votazione (IDinserzione, Username, Tipo)
            VALUES (?, ?, ?);  -- Tipo TRUE per upvote, FALSE per downvote

            UPDATE UTENTE u
            JOIN INSERZIONE i ON u.Username = i.Username
            SET u.UpvoteTotali = u.UpvoteTotali + CASE WHEN ? = TRUE THEN 1 ELSE 0 END,
                u.DownvoteTotali = u.DownvoteTotali + CASE WHEN ? = FALSE THEN 1 ELSE 0 END
            WHERE i.IDinserzione = ?;

            COMMIT;
            """;

    public static final String COMMENT_INSERTION = """
            INSERT INTO COMMENTO (dataPubblicazione, Contenuto, IDinserzione, Username)
            VALUES (CURDATE(), ?, ?, ?);
            """;

    public static final String REPORT_INSERTION = """
            START TRANSACTION;

            INSERT INTO Segnalazione_Inserzione (IDinserzione, Username, dataSegnalazione, Motivo)
            VALUES (?, ?, CURDATE(), ?);

            SET @User = (SELECT Username FROM INSERZIONE WHERE IDinserzione = ?);

            UPDATE UTENTE
            SET Segnalato = TRUE
            WHERE Username = @User AND Segnalato = FALSE;

            COMMIT;
            """;

    public static final String REPORT_COMMENT = """
            START TRANSACTION;

            INSERT INTO Segnalazione_Commento (IDcommento, Username, dataSegnalazione, Motivo)
            VALUES (?, ?, CURDATE(), ?);

            SET @User = (SELECT Username FROM COMMENTO WHERE IDcommento = ?);

            UPDATE UTENTE
            SET Segnalato = TRUE
            WHERE Username = @User AND Segnalato = FALSE;

            COMMIT;
            """;

    public static final String SHOW_CATEGORY = """
            START TRANSACTION;

                SET @Categoria = (
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
                );

                -- Se la categoria è Personaggio, cerca altre inserzioni di Personaggi
                IF @Categoria = 'Personaggio' THEN
                    SELECT i.*
                    FROM INSERZIONE i
                    JOIN CREAZIONE c ON i.IDcreazione = c.IDcreazione
                    JOIN PERSONAGGIO p ON c.IDcreazione = p.IDcreazione
                    WHERE i.IDinserzione <> ?;

                -- Se la categoria è Mostro, cerca altre inserzioni di Mostri
                ELSEIF @Categoria = 'Mostro' THEN
                    SELECT i.*
                    FROM INSERZIONE i
                    JOIN CREAZIONE c ON i.IDcreazione = c.IDcreazione
                    JOIN MOSTRO m ON c.IDcreazione = m.IDcreazione
                    WHERE i.IDinserzione <> ?;

                -- Se non c'è nessuna categoria, restituisci un messaggio
                ELSE
                    SELECT 'Questa creazione non appartiene a nessuna categoria';
                END IF;

            COMMIT;
            """;

    public static final String SHOW_SUBCATEGORY = """
            START TRANSACTION;

            SELECT s.Nome AS Sottocategoria, c.*, p.*, m.*
            FROM INSERZIONE i
            LEFT JOIN Appartenenza a ON i.IDinserzione = a.IDinserzione
            LEFT JOIN SOTTOCATEGORIA s ON a.IDsottocategoria = s.IDsottocategoria
            JOIN CREAZIONE c ON i.IDcreazione = c.IDcreazione
            LEFT JOIN PERSONAGGIO p ON c.IDcreazione = p.IDcreazione
            LEFT JOIN MOSTRO m ON c.IDcreazione = m.IDcreazione
            WHERE i.IDinserzione = ?;

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

            COMMIT;
            """;
    public static final String TOP_CREATORS = """
            SELECT Username, (UpvoteTotali - DownvoteTotali) AS Voti Totali
            FROM UTENTE
            ORDER BY Voti Totali DESC
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
            START TRANSACTION;

            INSERT INTO Moderazione (AdminName, Username, Tipo)
            VALUES (?, ?, ?);

            UPDATE UTENTE
            SET Segnalato = FALSE
            WHERE Username = @Username AND Segnalato = TRUE;

            COMMIT;
            """;
}

package db_lab.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Creation {

    public final int idCreazione;
    public final String nome;
    public final String descrizione;
    public final int forza;
    public final int destrezza;
    public final int costituzione;
    public final int intelligenza;
    public final int saggezza;
    public final int carisma;
    public final String classe;
    public final String razza;
    public final int livello;
    public final String taglia;
    public final int difficolta;
    public final String tipo;

    public Creation(int idCreazione, String nome, String descrizione,
            int forza, int destrezza, int costituzione, int intelligenza, int saggezza, int carisma,
            String classe, String razza, int livello,
            String taglia, int difficolta, String tipo) {
        this.idCreazione = idCreazione;
        this.nome = nome;
        this.descrizione = descrizione;
        this.forza = forza;
        this.destrezza = destrezza;
        this.costituzione = costituzione;
        this.intelligenza = intelligenza;
        this.saggezza = saggezza;
        this.carisma = carisma;
        this.classe = classe;
        this.razza = razza;
        this.livello = livello;
        this.taglia = taglia;
        this.difficolta = difficolta;
        this.tipo = tipo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idCreazione, this.nome, this.descrizione, this.forza, this.destrezza,
                this.costituzione, this.intelligenza, this.saggezza, this.carisma, this.classe, this.razza,
                this.livello, this.taglia, this.difficolta, this.tipo);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        } else if (other instanceof Creation) {
            var p = (Creation) other;
            return (idCreazione == p.idCreazione &&
                    nome.equals(p.nome) &&
                    descrizione.equals(p.descrizione) &&
                    forza == p.forza &&
                    destrezza == p.destrezza &&
                    costituzione == p.costituzione &&
                    intelligenza == p.intelligenza &&
                    saggezza == p.saggezza &&
                    carisma == p.carisma &&
                    classe.equals(p.classe) &&
                    razza.equals(p.razza) &&
                    livello == p.livello &&
                    taglia.equals(p.taglia) &&
                    difficolta == p.difficolta &&
                    tipo.equals(p.tipo));
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return Printer.stringify("Creation: ", List.of(Printer.field("idCreazione", idCreazione),
                Printer.field("nome", nome), Printer.field("descrizione", descrizione),
                Printer.field("forza", forza), Printer.field("destrezza", destrezza),
                Printer.field("costituzione", costituzione), Printer.field("intelligenza", intelligenza),
                Printer.field("saggezza", saggezza), Printer.field("carisma", carisma),
                Printer.field("classe", classe), Printer.field("razza", razza),
                Printer.field("livello", livello), Printer.field("taglia", taglia),
                Printer.field("difficolta", difficolta), Printer.field("tipo", tipo)));
    }

    public final class DAO {

        public static void createCharacter(Connection connection, String nome, String descrizione, int forza,
                int destrezza, int costituzione, int intelligenza, int saggezza, int carisma, int idRaccolta,
                String classe, String razza, int livello) {
            try (PreparedStatement statement = connection.prepareStatement(Queries.CHARACTER_CREATION)) {
                statement.setString(1, nome);
                statement.setString(2, descrizione);
                statement.setInt(3, forza);
                statement.setInt(4, destrezza);
                statement.setInt(5, costituzione);
                statement.setInt(6, intelligenza);
                statement.setInt(7, saggezza);
                statement.setInt(8, carisma);
                statement.setInt(9, idRaccolta);
                statement.setString(10, classe);
                statement.setString(11, razza);
                statement.setInt(12, livello);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static void createMonster(Connection connection, String nome, String descrizione, int forza,
                int destrezza,
                int costituzione, int intelligenza, int saggezza, int carisma, int idRaccolta, String taglia,
                int difficolta, String tipo) {
            try (PreparedStatement statement = connection.prepareStatement(Queries.MONSTER_CREATION)) {
                statement.setString(1, nome);
                statement.setString(2, descrizione);
                statement.setInt(3, forza);
                statement.setInt(4, destrezza);
                statement.setInt(5, costituzione);
                statement.setInt(6, intelligenza);
                statement.setInt(7, saggezza);
                statement.setInt(8, carisma);
                statement.setInt(9, idRaccolta);
                statement.setString(10, taglia);
                statement.setInt(11, difficolta);
                statement.setString(12, tipo);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static void publishCreation(Connection connection, int idCreazione, String username) {
            try (PreparedStatement statement = connection.prepareStatement(Queries.PUBLISH_CREATION)) {
                statement.setInt(1, idCreazione);
                statement.setString(2, username);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static List<Creation> showCategories(Connection connection, int idInserzione) {
            List<Creation> result = new ArrayList<>();
            String query = Queries.SHOW_CATEGORY;
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idInserzione);
                statement.setInt(2, idInserzione);
                statement.setInt(3, idInserzione);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int idCreazione = resultSet.getInt("IDcreazione");
                        String nome = resultSet.getString("Nome");
                        String descrizione = resultSet.getString("Descrizione");
                        int forza = resultSet.getInt("Forza");
                        int destrezza = resultSet.getInt("Destrezza");
                        int costituzione = resultSet.getInt("Costituzione");
                        int intelligenza = resultSet.getInt("Intelligenza");
                        int saggezza = resultSet.getInt("Saggezza");
                        int carisma = resultSet.getInt("Carisma");
                        String classe = resultSet.getString("Classe");
                        String razza = resultSet.getString("Razza");
                        int livello = resultSet.getInt("Livello");
                        String taglia = resultSet.getString("Taglia");
                        int difficolta = resultSet.getInt("Difficolta");
                        String tipo = resultSet.getString("Tipo");

                        result.add(new Creation(
                                idCreazione,
                                nome,
                                descrizione,
                                forza,
                                destrezza,
                                costituzione,
                                intelligenza,
                                saggezza,
                                carisma,
                                classe != null ? classe : "",
                                razza != null ? razza : "",
                                livello,
                                taglia != null ? taglia : "",
                                difficolta,
                                tipo != null ? tipo : ""));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return result;
        }

        public static List<Creation> showSubcategory(Connection connection, int idInserzione) {
            List<Creation> result = new ArrayList<>();
            String query = Queries.SHOW_SUBCATEGORY;
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idInserzione);
                boolean isResultSet = statement.execute();
                while (isResultSet) {
                    try (ResultSet resultSet = statement.getResultSet()) {
                        while (resultSet.next()) {
                            result.add(resultSet.getString("Sottocategoria"));
                            int idCreazione = resultSet.getInt("IDcreazione");
                            String nome = resultSet.getString("Nome");
                            String descrizione = resultSet.getString("Descrizione");
                            int forza = resultSet.getInt("Forza");
                            int destrezza = resultSet.getInt("Destrezza");
                            int costituzione = resultSet.getInt("Costituzione");
                            int intelligenza = resultSet.getInt("Intelligenza");
                            int saggezza = resultSet.getInt("Saggezza");
                            int carisma = resultSet.getInt("Carisma");
                            String classe = resultSet.getString("Classe");
                            String razza = resultSet.getString("Razza");
                            int livello = resultSet.getInt("Livello");
                            String taglia = resultSet.getString("Taglia");
                            int difficolta = resultSet.getInt("Difficolta");
                            String tipo = resultSet.getString("Tipo");

                            result.add(new Creation(
                                    idCreazione,
                                    nome,
                                    descrizione,
                                    forza,
                                    destrezza,
                                    costituzione,
                                    intelligenza,
                                    saggezza,
                                    carisma,
                                    classe != null ? classe : "",
                                    razza != null ? razza : "",
                                    livello,
                                    taglia != null ? taglia : "",
                                    difficolta,
                                    tipo != null ? tipo : ""));
                        }
                    }
                    isResultSet = statement.getMoreResults();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}
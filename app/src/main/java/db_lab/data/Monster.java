package db_lab.data;

import java.util.List;

public record Monster(
        Creation creation,
        String size,
        int challengeRating,
        String type) {
    @Override
    public String toString() {
        return Printer.stringify("Monster: ", List.of(
                Printer.field("idCreation", this.creation.idCreation()),
                Printer.field("creationDate", this.creation.creationDate()),
                Printer.field("name", this.creation.name()),
                Printer.field("description", this.creation.description()),
                Printer.field("strength", this.creation.strength()),
                Printer.field("dexterity", this.creation.dexterity()),
                Printer.field("constitution", this.creation.constitution()),
                Printer.field("intelligence", this.creation.intelligence()),
                Printer.field("wisdom", this.creation.wisdom()),
                Printer.field("charisma", this.creation.charisma()),
                Printer.field("downloads", this.creation.downloads()),
                Printer.field("size", this.size),
                Printer.field("challengeRating", this.challengeRating),
                Printer.field("type", this.type)));
    }
}
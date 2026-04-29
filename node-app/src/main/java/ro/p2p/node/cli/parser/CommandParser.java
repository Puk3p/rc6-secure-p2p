package ro.p2p.node.cli.parser;

public class CommandParser {

    public ParsedCommand parse(String line) {
        if (line == null || line.isBlank()) {
            return new ParsedCommand("", new String[0]);
        }
        String trimmed = line.trim();
        String[] parts = trimmed.split("\\s+", 3);
        if (parts.length == 1) {
            return new ParsedCommand(parts[0], new String[0]);
        }
        if (parts.length == 2) {
            return new ParsedCommand(parts[0], new String[] {parts[1]});
        }
        return new ParsedCommand(parts[0], new String[] {parts[1], parts[2]});
    }

    public record ParsedCommand(String command, String[] arguments) {}
}

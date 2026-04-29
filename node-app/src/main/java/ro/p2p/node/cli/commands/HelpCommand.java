package ro.p2p.node.cli.commands;

import ro.p2p.node.app.CommandDispatcher.RunnableCommand;

public class HelpCommand implements RunnableCommand {

    @Override
    public boolean run(String[] args) {
        System.out.println("Available commands:");
        System.out.println("  help                         Show this help message");
        System.out.println("  peers                        Show configured and connected peers");
        System.out.println("  send <peerId> <message>      Send encrypted text message");
        System.out.println("  send-file <peerId> <path>    Send file bytes as encrypted chunks");
        System.out.println("  exit                         Stop this node");
        return true;
    }
}

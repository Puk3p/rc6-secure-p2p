package ro.p2p.node.cli.commands;

import ro.p2p.node.app.CommandDispatcher.RunnableCommand;

public class ExitCommand implements RunnableCommand {

    @Override
    public boolean run(String[] args) {
        return false;
    }
}

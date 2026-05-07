package com.akira.server.commands.interfaces;

public interface DelegatingCommand {
    Command getDelegate();
}

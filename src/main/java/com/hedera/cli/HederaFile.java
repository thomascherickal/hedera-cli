package com.hedera.cli;

import com.hedera.cli.config.InputReader;
import com.hedera.cli.defaults.CliDefaults;
import com.hedera.cli.hedera.file.File;
import com.hedera.cli.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class HederaFile extends CliDefaults {

    @Autowired
    ShellHelper shellHelper;

    @Autowired
    ApplicationContext context;

    @Autowired
    InputReader inputReader;

    public HederaFile() {
    }

    @ShellMethodAvailability("isDefaultNetworkAndAccountSet")
    @ShellMethod(value = "manage hedera file")
    public void file(
            @ShellOption(defaultValue = "") String subCommand,
            @ShellOption(defaultValue = "", arity = -1) String... args) {
        File file = new File();
        file.handle(context, inputReader, subCommand, args);
    }
}
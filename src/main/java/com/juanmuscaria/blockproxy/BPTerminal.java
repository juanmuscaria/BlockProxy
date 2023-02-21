package com.juanmuscaria.blockproxy;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.MaskingCallback;
import org.jline.reader.ParsedLine;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class BPTerminal implements Runnable {
    Terminal terminal;
    private EnetProxy proxy;

    public BPTerminal(EnetProxy proxy) throws IOException {
        this.proxy = proxy;
        TerminalBuilder builder = TerminalBuilder.builder();
        terminal = builder.jna(false).jansi(false).dumb(true).build();
    }

    @Override
    public void run() {
        LineReader reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .variable(LineReader.SECONDARY_PROMPT_PATTERN, "%M%P > ")
                .variable(LineReader.INDENTATION, 2)
                .option(LineReader.Option.INSERT_BRACKET, true)
                .build();
        String line = null;
        while (true) {
            try {
                line = reader.readLine("proxy>", null, (MaskingCallback) null, null);
                line = line.trim();
                ParsedLine pl = reader.getParser().parse(line, 0);
                String[] argv = pl.words().subList(1, pl.words().size()).toArray(new String[0]);
                if ("sendraw".equalsIgnoreCase(pl.word())) {
                    if (argv.length == 2) {
                        if ("server".equalsIgnoreCase(argv[0])) {
                            proxy.injectedToServer.add(argv[1]);
                        } else if ("client".equalsIgnoreCase(argv[0])) {
                            proxy.injectedToClient.add(argv[1]);
                        } else {
                            terminal.writer().println("Usage: sendraw <server|client> <hex data>");
                        }
                    } else {
                        terminal.writer().println("Usage: sendraw <server|client> <hex data>");
                    }
                } else if ("toggle".equalsIgnoreCase(pl.word())) {
                    if (argv.length == 1) {
                        if ("server".equalsIgnoreCase(argv[0])) {
                            proxy.dumpServer = !proxy.dumpServer;;
                            terminal.writer().println("Log toggled to:" + proxy.dumpServer);
                        } else if ("client".equalsIgnoreCase(argv[0])) {
                            proxy.dumpClient = !proxy.dumpClient;
                            terminal.writer().println("Log toggled to:" + proxy.dumpClient);
                        } else {
                            terminal.writer().println("Usage: toggle <server|client>");
                        }
                    } else {
                        terminal.writer().println("Usage: toggle <server|client>");
                    }
                } else {
                    terminal.writer().println("Command not found: " + pl.word());
                }
                terminal.flush();
            } catch (Exception ignored) { }

        }

    }
}

package Eliza;

import java.awt.*;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ElizaMain {
    final boolean echoInput = false;
    final boolean printData = false;
    final boolean printKeys = false;
    final boolean printSyns = false;
    final boolean printPrePost = false;
    final boolean printInitialFinal = false;
    KeyList keys = new KeyList();
    SynList syns = new SynList();
    PrePostList pre = new PrePostList();
    PrePostList post = new PrePostList();
    String initial = "Hello.";
    String finl = "Goodbye.";
    WordList quit = new WordList();
    KeyStack keyStack = new KeyStack();
    Mem mem = new Mem();
    DecompList lastDecomp;
    ReasembList lastReasemb;
    boolean finished = false;
    static final int success = 0;
    static final int failure = 1;
    static final int gotoRule = 2;
    TextArea textarea;
    TextField textfield;

    public boolean finished() {
        return this.finished;
    }

    public void collect(String s) {
        String[] lines = new String[4];
        if (EString.match(s, "*reasmb: *", lines)) {
            if (this.lastReasemb == null) {
                System.out.println("Error: no last reasemb");
                return;
            }
            this.lastReasemb.add(lines[1]);
        } else if (EString.match(s, "*decomp: *", lines)) {
            if (this.lastDecomp == null) {
                System.out.println("Error: no last decomp");
                return;
            }
            this.lastReasemb = new ReasembList();
            String temp = new String(lines[1]);
            if (EString.match(temp, "$ *", lines)) {
                this.lastDecomp.add(lines[0], true, this.lastReasemb);
            } else {
                this.lastDecomp.add(temp, false, this.lastReasemb);
            }
        } else if (EString.match(s, "*key: * #*", lines)) {
            this.lastDecomp = new DecompList();
            this.lastReasemb = null;
            int n = 0;
            if (lines[2].length() != 0) {
                try {
                    n = Integer.parseInt(lines[2]);
                } catch (NumberFormatException localNumberFormatException) {
                    System.out.println("Number is wrong in key: " + lines[2]);
                }
            }
            this.keys.add(lines[1], n, this.lastDecomp);
        } else if (EString.match(s, "*key: *", lines)) {
            this.lastDecomp = new DecompList();
            this.lastReasemb = null;
            this.keys.add(lines[1], 0, this.lastDecomp);
        } else if (EString.match(s, "*synon: * *", lines)) {
            WordList words = new WordList();
            words.add(lines[1]);
            s = lines[2];
            while (EString.match(s, "* *", lines)) {
                words.add(lines[0]);
                s = lines[1];
            }
            words.add(s);
            this.syns.add(words);
        } else if (EString.match(s, "*pre: * *", lines)) {
            this.pre.add(lines[1], lines[2]);
        } else if (EString.match(s, "*post: * *", lines)) {
            this.post.add(lines[1], lines[2]);
        } else if (EString.match(s, "*initial: *", lines)) {
            this.initial = lines[1];
        } else if (EString.match(s, "*final: *", lines)) {
            this.finl = lines[1];
        } else if (EString.match(s, "*quit: *", lines)) {
            this.quit.add(" " + lines[1] + " ");
        } else {
            System.out.println("Unrecognized input: " + s);
        }
    }

    public void print() {
    }

    public String processInput(String s) {
        s = EString.translate(s, "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                "abcdefghijklmnopqrstuvwxyz");
        s = EString.translate(s, "@#$%^&*()_-+=~`{[}]|:;<>\\\"",
                "                          ");
        s = EString.translate(s, ",?!", "...");

        s = EString.compress(s);
        String[] lines = new String[2];
        while (EString.match(s, "*.*", lines)) {
            String reply = sentence(lines[0]);
            if (reply != null) {
                return reply;
            }
            s = EString.trim(lines[1]);
        }
        if (s.length() != 0) {
            String reply = sentence(s);
            if (reply != null) {
                return reply;
            }
        }
        String m = this.mem.get();
        if (m != null) {
            return m;
        }
        Key key = this.keys.getKey("xnone");
        if (key != null) {
            Key dummy = null;
            String reply = decompose(key, s, dummy);
            if (reply != null) {
                return reply;
            }
        }
        return "I am at a loss for words.";
    }

    String sentence(String s) {
        s = this.pre.translate(s);
        s = EString.pad(s);
        if (this.quit.find(s)) {
            this.finished = true;
            return this.finl;
        }
        this.keys.buildKeyStack(this.keyStack, s);
        for (int i = 0; i < this.keyStack.keyTop(); i++) {
            Key gotoKey = new Key();
            String reply = decompose(this.keyStack.key(i), s, gotoKey);
            if (reply != null) {
                return reply;
            }
            while (gotoKey.key() != null) {
                reply = decompose(gotoKey, s, gotoKey);
                if (reply != null) {
                    return reply;
                }
            }
        }
        return null;
    }

    String decompose(Key key, String s, Key gotoKey) {
        String[] reply = new String[10];
        for (int i = 0; i < key.decomp().size(); i++) {
            Decomp d = (Decomp) key.decomp().elementAt(i);
            String pat = d.pattern();
            if (this.syns.matchDecomp(s, pat, reply)) {
                String rep = assemble(d, reply, gotoKey);
                if (rep != null) {
                    return rep;
                }
                if (gotoKey.key() != null) {
                    return null;
                }
            }
        }
        return null;
    }

    String assemble(Decomp d, String[] reply, Key gotoKey) {
        String[] lines = new String[3];
        d.stepRule();
        String rule = d.nextRule();
        if (EString.match(rule, "goto *", lines)) {
            gotoKey.copy(this.keys.getKey(lines[0]));
            if (gotoKey.key() != null) {
                return null;
            }
            System.out.println("Goto rule did not match key: " + lines[0]);
            return null;
        }
        String work = "";
        while (EString.match(rule, "* (#)*", lines)) {
            rule = lines[2];
            int n = 0;
            try {
                n = Integer.parseInt(lines[1]) - 1;
            } catch (NumberFormatException localNumberFormatException) {
                System.out.println("Number is wrong in reassembly rule " + lines[1]);
            }
            if ((n < 0) || (n >= reply.length)) {
                System.out.println("Substitution number is bad " + lines[1]);
                return null;
            }
            reply[n] = this.post.translate(reply[n]);
            work = work + lines[0] + " " + reply[n];
        }
        work = work + rule;
        if (d.mem()) {
            this.mem.save(work);
            return null;
        }
        return work;
    }

    public void response(String str) {
        this.textarea.appendText(str);
        this.textarea.appendText("\n");
    }

    int readScript(boolean local, String script) {
        try {
            DataInputStream in;
            if (local) {
                in = new DataInputStream(new FileInputStream(script));
            } else {
                try {
                    URL url = new URL(script);
                    URLConnection connection = url.openConnection();
                    in = new DataInputStream(connection.getInputStream());
                } catch (MalformedURLException localMalformedURLException) {
                    System.out.println("The URL is malformed: " + script);
                    return 1;
                } catch (IOException localIOException1) {
                    System.out.println("Could not read script file.");
                    return 1;
                }
            }
            for (; ; ) {
                String s = in.readLine();
                if (s == null) {
                    break;
                }
                collect(s);
            }
        } catch (IOException localIOException2) {
            System.out.println("There was a problem reading the script file.");
            System.out.println("Tried " + script);
            return 1;
        }
        return 0;
    }

    int runProgram(String test, Panel w) {
        if (w != null) {
            w.setLayout(new BorderLayout(15, 15));

            this.textarea = new TextArea(10, 40);
            this.textarea.setEditable(false);

            w.add("Center", this.textarea);

            this.textfield = new TextField(15);
            w.add("South", this.textfield);

            w.resize(600, 300);
            w.show();

            String hello = "Hello.";
            response(">> " + hello);
            response(processInput(hello));
            this.textfield.requestFocus();
        } else {
            try {
                DataInputStream in = new DataInputStream(new FileInputStream(test));

                String s = "Hello.";
                for (; ; ) {
                    System.out.println(">> " + s);
                    String reply = processInput(s);
                    System.out.println(reply);
                    if (!this.finished) {
                        s = in.readLine();
                        if (s == null) {
                            break;
                        }
                    }
                }
            } catch (IOException localIOException) {
                System.out.println("Problem reading test file.");
                return 1;
            }
        }
        return 0;
    }

    public boolean handleEvent(Event event) {
        switch (event.id) {
            case 1001:
                if (event.target == this.textfield) {
                    String input = (String) event.arg;
                    String reply = processInput(input);
                    this.textfield.setText("");
                    response(">> " + input);
                    response(reply);

                    return true;
                }
                break;
        }
        return false;
    }
}

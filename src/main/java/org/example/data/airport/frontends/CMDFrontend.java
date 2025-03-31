package org.example.data.airport.frontends;

import org.example.ctrl.ConnectionFactory;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class CMDFrontend {
    private static Pattern isNumber = Pattern.compile("^([0-9]+[,.]?)*$");
    private static final int rowsPerPage = 10;
    private static final boolean format = true;
    private static final String query = "SELECT * FROM ? OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

    private static boolean contains(String[] arr, String s) {
        return Arrays.asList(arr).contains(s);
    }

    private static String format(String s, int maxLen) {
        if (isNumber.matcher(s).find()) {
            s = String.format("%" + maxLen + "s", s);
        }
        return s;
    }

    private record RawTable(String[][] tokens, int[] maxColLens) {};

    private static int curOffset = 0;
    private static ResultSet queryDb(String tableName, int rowsPerPage) throws SQLException {
        String fullQuery = "SELECT * FROM " + tableName + " OFFSET " + curOffset + " ROWS" + ((rowsPerPage != -1) ? " FETCH NEXT " + rowsPerPage + " ROWS ONLY" : "");
        curOffset += rowsPerPage;
        return ConnectionFactory.getInstance().createStatement().executeQuery(fullQuery);
    }
    private static ResultSet queryDb(String tableName) throws SQLException {
        return queryDb(tableName, rowsPerPage);
    }

    private static RawTable parseTable(ResultSet rs) throws SQLException {
        ArrayList<String[]> ret = new ArrayList<>(2); // suboptimal allocation
        final int cols = rs.getMetaData().getColumnCount();
        ret.add(new String[cols]);
        ret.add(new String[cols]);
        int[] maxColLens = new int[cols];
        for (int i = 1; i <= cols; i++) { // header
            String tmp = rs.getMetaData().getColumnName(i);
            ret.get(0)[i-1] = tmp;
            if (tmp.length() > maxColLens[i-1]) maxColLens[i-1] = tmp.length();
        }
        for (int i = 1; i <= cols; i++) { // types
            String tmp = rs.getMetaData().getColumnTypeName(i);
            ret.get(1)[i-1] = tmp;
            if (tmp.length() > maxColLens[i-1]) maxColLens[i-1] = tmp.length();
        }
        while (rs.next()) {
            ret.add(new String[cols]);
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                String tmp = rs.getObject(i) == null ? "null" : rs.getObject(i).toString();
                ret.get(ret.size()-1)[i-1] = tmp;
                if (tmp.length() > maxColLens[i-1]) maxColLens[i-1] = tmp.length();
            }
        }
        for (int i = 1; i < ret.size(); i++) {
            for (int j = 0; j < ret.get(i).length; j++) {
                ret.get(i)[j] = format(ret.get(i)[j], maxColLens[j]);
            }
        }
        for (int i = 0; i < maxColLens.length; i++) {
            maxColLens[i] = (maxColLens[i]/tabWidth+1)*tabWidth;
        }
        String[][] tmp = new String[ret.size()][cols];
        return new RawTable(ret.toArray(tmp), maxColLens);
    }
    private static void printTable(String[][] tokens, int[] maxLens) {
        clearScreen();
        for (int i = 0; i < tokens.length; i++) {
            for (int j = 0; j < tokens[i].length; j++) {
                System.out.print(tokens[i][j]);
                int curColLen = tokens[i][j].length();
                do {
                    System.out.print('\t');
                    curColLen = (curColLen/tabWidth+1)*tabWidth;
                }while (curColLen < maxLens[j]);
            }
            System.out.println();
        }
    }
    private static void printTableFormatted(String[][] tokens, int[] maxLens) {
        clearScreen();
        String[] lines = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            StringBuilder line = new StringBuilder();
            for (int j = 0; j < tokens[i].length; j++) {
                line.append("| ");
                line.append(tokens[i][j]);
                int curColLen = tokens[i][j].length() + 2;
                //System.out.print("\t".repeat((maxLens[j]-curColLen)/tabWidth+1));
                while (curColLen < maxLens[j]) {
                    line.append('\t');
                    curColLen = (curColLen+tabWidth) - (curColLen+tabWidth)%tabWidth;
                }
                if (curColLen > maxLens[j]) {
                    //maxLens[j] = curColLen;
                    line.append('\t');
                    maxLens[j] = (curColLen+tabWidth) - (curColLen+tabWidth)%tabWidth;
                }
            }
            line.append(" |");
            lines[i] = line.toString();
        }
        int sum = 0;
        for (int maxLen : maxLens) {
            sum += maxLen;
        }
        System.out.print('+');
        System.out.print("-".repeat(sum));
        System.out.println('+');
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.print('+');
        System.out.print("-".repeat(sum));
        System.out.println('+');
    }
    private static void printTable(RawTable rt) {
        if (format)
            printTableFormatted(rt.tokens, rt.maxColLens);
        else
            printTable(rt.tokens, rt.maxColLens);
    }

    private static Terminal t;
    private static void clearScreen() {
        t.puts(InfoCmp.Capability.clear_screen);
        t.flush();
    }

    private static void switchToTableView(String tableName) throws IOException {
        System.out.printf("[n]ext %d, [p]revious %d, [a]ll, [q]uit\n", rowsPerPage, rowsPerPage);
        t.enterRawMode();
        while (true) {
            try {
                switch (t.reader().read()) {
                    case 'n':
                        printTable(parseTable(queryDb(tableName)));
                        break;
                    case 'p':
                        if (curOffset >= rowsPerPage)
                            curOffset -= rowsPerPage*2;
                        printTable(parseTable(queryDb(tableName)));
                        break;
                    case 'a':
                        printTable(parseTable(queryDb(tableName, -1)));
                        break;
                    case 'q':
                        curOffset = 0;
                        return;
                }
                System.out.printf("[n]ext %d, [p]revious %d, [a]ll, [q]uit\n", rowsPerPage, rowsPerPage);
                System.out.flush();
            } catch (IOException e) {
                System.err.println("Error when reading: " + e);
            } catch (SQLException e) {
                System.err.println("DB Error: " + e);
            }
        }
    }

    private static int tabWidth = 4;
    public static void main(String[] args) throws IOException {
        Scanner s = new Scanner(System.in);
        t = TerminalBuilder.terminal();
        LineReaderBuilder.builder().terminal(t).variable(LineReader.TAB_WIDTH, tabWidth).build();
        try {
            while (true) {
                System.out.println("Tables:");
                String[] availTables = ConnectionFactory.getInstance().getTables();
                int tIdx = 1;
                for (String table : availTables) {
                    System.out.printf("\t%d.%s\n", tIdx++, table);
                }
                System.out.print("(type 'quit' to exit)>");
                String selectedTable = s.nextLine().toUpperCase();
                if ("QUIT".equals(selectedTable)) {
                    t.close();
                    return;
                }

                int tableIndex;
                while ((tableIndex = parsePotentialIndex(selectedTable)) == -1 && !contains(availTables, selectedTable)) {
                    System.out.println("Unknown table!");
                    System.out.print(">");
                    selectedTable = s.nextLine();
                }

                selectedTable = tableIndex != -1 ? availTables[tableIndex-1] : selectedTable;
                RawTable rt = parseTable(queryDb(selectedTable));
                printTable(rt);
                switchToTableView(selectedTable);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        } finally {
            t.close();
        }
    }

    private static int parsePotentialIndex(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

package com.cxj.hibernate;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Created by vipcxj on 2018/8/24.
 */
public class SqlUtils {

    public static String checkProcedureExists(String procedure) {
        return "SELECT 1 FROM sysobjects WHERE id = object_id('" + procedure + "') AND type = 'P'";
    }

    public static String exists(String expr) {
        return "EXISTS ( " + expr + " )";
    }

    public static String indentCode(String indent, String code) {
        return indent + code.replaceAll("([\\r\\n]+)", "$1" + Matcher.quoteReplacement(indent));
    }

    public static String ifPlainBlock(String indent, String cond, String action, String elseAction) {
        String sb = indent + "IF " + cond + "\n" +
                indent + "BEGIN\n" +
                indentCode(indent + "  ", action) + "\n" +
                indent + "END";
        if (elseAction != null && !elseAction.isEmpty()) {
            sb += indent + "ELSE\n" +
                    indent + "BEGIN" +
                    indentCode(indent + "  ", elseAction) + "\n" +
                    indent + "END";
        }
        return sb;
    }

    public static String ifBlock(String indent, String ...exprs) {
        if (exprs.length < 2) {
            throw new IllegalArgumentException("At least two expr: condition and statement");
        } else if (exprs.length == 2) {
            return ifPlainBlock(indent, exprs[0], exprs[1], null);
        } else if (exprs.length == 3) {
            return ifPlainBlock(indent, exprs[0], exprs[1], exprs[2]);
        } else {
            return ifPlainBlock(indent, exprs[0], exprs[1], ifBlock(indent + "  ", Arrays.copyOfRange(exprs, 2, exprs.length)));
        }
    }

    public static String defineProcedure(@Nonnull String action, @Nonnull String name, String code, Map<String, String> paramsDefines, String indent) {
        action = action.toUpperCase();
        if (!action.equals("CREATE") && !action.equals("ALTER")) {
            throw new IllegalArgumentException("Invalid action. Must be 'CREATE' or 'ALTER'");
        }
        if (code == null || code.isEmpty()) {
            return "";
        }
        if (indent == null) {
            indent = "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append(action).append(" PROCEDURE ").append(HibernateUtils.quote(name)).append("\n");
        if (paramsDefines != null) {
            for (Map.Entry<String, String> entry : paramsDefines.entrySet()) {
                sb.append(indent).append("  @").append(entry.getKey()).append(" AS ").append(entry.getValue()).append(",\n");
            }
        }
        sb.append(indent).append("AS\n");
        sb.append(indent).append("BEGIN\n");
        sb.append(indentCode(indent + "  ", code));
        sb.append("\n");
        sb.append(indent).append("END\n");
        return sb.toString();
    }

    public static String declare(String varName, String varType) {
        return "DECLARE @" + varName + " " + varType + ";";
    }

    public static String declare(String varName, String varType, String defaultValue) {
        return "DECLARE @" + varName + " " + varType + " = " + defaultValue + ";";
    }

    public static String setVar(String varName, String value) {
        return "SET @" + varName + " = " + value + ";";
    }

    public static String transactionBlock(String code, String tranName, String indent) {
        if (indent == null) {
            indent = "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("BEGIN TRY\n");
        sb.append(indent).append("  BEGIN TRANSACTION");
        if (tranName != null && !tranName.isEmpty()) {
            sb.append(" ").append(tranName);
        }
        sb.append(";\n");
        sb.append(indentCode(indent + "    ", code));
        sb.append("\n");
        sb.append(indent).append("  COMMIT TRANSACTION;\n");
        sb.append(indent).append("END TRY\n");
        sb.append(indent).append("BEGIN CATCH\n");
        sb.append(indent).append("  IF @@TRANCOUNT > 0\n");
        sb.append(indent).append("    ROLLBACK");
        if (tranName != null && !tranName.isEmpty()) {
            sb.append(" ").append(tranName);
        }
        sb.append(";\n");
        sb.append(indent).append("  ;THROW\n");
        sb.append(indent).append("END CATCH");
        return sb.toString();
    }
}

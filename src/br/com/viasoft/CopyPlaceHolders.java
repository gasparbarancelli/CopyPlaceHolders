package br.com.viasoft;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.NonEmptyInputValidator;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CopyPlaceHolders extends com.intellij.openapi.actionSystem.AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        try {
            Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);
            Project project = anActionEvent.getProject();
            anActionEvent.getPresentation().setVisible(project != null && editor != null);

            String textoSelecionado = editor.getSelectionModel().getSelectedText();
            if (textoSelecionado == null) {
                textoSelecionado = editor.getDocument().getText();
            }
            textoSelecionado = textoSelecionado.replace("、", ",").replace("，", ",");

            String source = project.getBasePath() + "/src/main/resources/placeHolders.properties";
            Properties properties = new Properties();
            properties.load(new FileInputStream(new File(source)));

            Map<String, String> varList = new HashMap<>();

            Pattern pattern = Pattern.compile("\\$\\{\\w+\\}");
            Matcher matcher = pattern.matcher(textoSelecionado);
            while (matcher.find()) {
                String var = matcher.group();
                String value = varList.get(var);
                if (value == null) {
                    try {
                        String property = var.substring(2, var.length() - 1);
                        value = properties.get(property.toLowerCase()).toString();
                    } catch (Exception e) {
                        value = Messages.showInputDialog(
                                "Set value for " + var,
                                "Place Holder",
                                Messages.getQuestionIcon(),
                                "",
                                new NonEmptyInputValidator());
                    } finally {
                        varList.put(var, value);
                    }

                }
                textoSelecionado = textoSelecionado.replace(var, value);
            }

            CopyPasteManager.getInstance().setContents(new StringSelection(textoSelecionado));
        } catch (Exception ignore) {

        }
    }

}


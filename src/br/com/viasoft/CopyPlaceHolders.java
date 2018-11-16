package br.com.viasoft;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class CopyPlaceHolders extends com.intellij.openapi.actionSystem.AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        try {
            Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);
            String textoSelecionado = editor.getSelectionModel().getSelectedText();
            if (textoSelecionado == null) {
                textoSelecionado = editor.getDocument().getText();
            }
            textoSelecionado = textoSelecionado.replace("、", ",").replace("，", ",");

            String source = anActionEvent.getProject().getBasePath() + "/src/main/resources/placeHolders.properties";
            Properties properties = new Properties();
            properties.load(new FileInputStream(new File(source)));

            for (Object key : properties.keySet()) {
                textoSelecionado = textoSelecionado.replace("${" + key.toString().toUpperCase() + "}", properties.get(key).toString());
            }

            CopyPasteManager.getInstance().setContents(new StringSelection(textoSelecionado));
        } catch (Exception ignore) {

        }
    }

}


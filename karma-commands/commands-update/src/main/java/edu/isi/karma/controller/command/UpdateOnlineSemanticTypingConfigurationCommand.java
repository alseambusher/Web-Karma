package edu.isi.karma.controller.command;

import com.sun.org.apache.xpath.internal.operations.Bool;
import edu.isi.karma.config.ModelingConfigurationRegistry;
import edu.isi.karma.controller.update.AbstractUpdate;
import edu.isi.karma.controller.update.ErrorUpdate;
import edu.isi.karma.controller.update.UpdateContainer;
import edu.isi.karma.rep.Workspace;
import edu.isi.karma.view.VWorkspace;
import edu.isi.karma.webserver.ContextParametersRegistry;
import edu.isi.karma.webserver.ServletContextParameterMap;
import org.json.JSONStringer;
import org.json.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by alse on 9/29/16.
 */
public class UpdateOnlineSemanticTypingConfigurationCommand extends Command {

    private Boolean isSemanticLabelingOnline ;
    private String propertyName = "online.semantic.typing";

    private static Logger logger = LoggerFactory.getLogger(UpdateUIConfigurationCommand.class);

    protected UpdateOnlineSemanticTypingConfigurationCommand(String id, String model, Boolean property) {
        super(id, model);
        this.isSemanticLabelingOnline = property;
    }

    @Override
    public String getCommandName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getTitle() {
        return "Set Modeling Configuration";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.notInHistory;
    }

    @Override
    public UpdateContainer doIt(Workspace workspace) throws CommandException {
        UpdateContainer uc = new UpdateContainer();
        try{

            uc.add(new AbstractUpdate() {
                @Override
                public void generateJson(String prefix, PrintWriter pw, VWorkspace vWorkspace) {
                    try {
                        String fileName = ContextParametersRegistry.getInstance().getContextParameters(ContextParametersRegistry.getInstance().getDefault().getId()).getParameterValue(ServletContextParameterMap.ContextParameter.USER_CONFIG_DIRECTORY) + "/modeling.properties";
                        BufferedReader file = new BufferedReader(new FileReader(fileName));
                        String line;
                        String modelingPropertiesContent = "";
                        while ((line = file.readLine()) != null) modelingPropertiesContent += line + '\n';
                        file.close();

                        modelingPropertiesContent = modelingPropertiesContent.replaceFirst(propertyName + "=" + Boolean.toString(!isSemanticLabelingOnline), propertyName + "=" + Boolean.toString(isSemanticLabelingOnline));

                        FileOutputStream fileOut = new FileOutputStream(fileName);
                        fileOut.write(modelingPropertiesContent.getBytes());
                        fileOut.close();

                        ModelingConfigurationRegistry.getInstance().getModelingConfiguration(ContextParametersRegistry.getInstance().getDefault().getId()).setOnlineSemanticTypingEnabled(isSemanticLabelingOnline);
                        JSONStringer jsonStr = new JSONStringer();

                        JSONWriter writer = jsonStr.object();
                        writer.key("updateType").value("UpdateOnlineSemanticType");
                        writer.key("isSemanticLabelingOnline").value(Boolean.toString(isSemanticLabelingOnline));
                        writer.endObject();
                        pw.print(writer.toString());
                    } catch (Exception e) {
                        logger.error("Error updating Modeling Configuraion", e);
                    }
                }
            });
        }  catch (Exception e) {
			logger.error("Error updating Modeling Configuraion:" , e);
			uc.add(new ErrorUpdate("Error updating Modeling Configuraion"));
		}
        return uc;
    }

    @Override
    public UpdateContainer undoIt(Workspace workspace) {
        return null;
    }
}

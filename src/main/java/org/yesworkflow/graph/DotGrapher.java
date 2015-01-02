package org.yesworkflow.graph;

import java.io.FileWriter;
import java.io.Writer;

import org.yesworkflow.model.Channel;
import org.yesworkflow.model.Port;
import org.yesworkflow.model.Program;
import org.yesworkflow.model.Workflow;

public class DotGrapher implements Grapher  {

	final static String EOL = System.getProperty("line.separator");
	
    private Workflow workflow = null;
    private GraphType graphType = null;
    private String graphText = null;

    @SuppressWarnings("unused")
    private GraphFormat graphFormat = null;
    
    @Override
    public DotGrapher workflow(Workflow workflow) {
        this.workflow = workflow;
        return this;
    }

    @Override
    public DotGrapher type(GraphType graphType) {
        this.graphType = graphType;
        return this;
    }
    
    @Override
    public Grapher format(GraphFormat format) {
       this.graphFormat = format;
       return this;
    }
    
	public String toString() {
        return graphText;
    }
    
    @Override
    public DotGrapher graph() {
        
        switch(graphType) {
        
            case DATA_FLOW_GRAPH:
                this.graphText = renderWorkflowGraph();
                break;
            
            case DATA_DEPENDENCY_GRAPH:
                break;
            
            case ABSTRACT_PROVENANCE_GRAPH:
                break;
        }
        
        return this;
    }
    
    private String renderWorkflowGraph() {

	    DotBuilder dot = new DotBuilder();
		
		dot.begin();

		// draw a box for each program in the workflow
		dot.shape("box").fillcolor("#CCFFCC");		
		for (Program p : workflow.programs) dot.node(p.beginComment.programName);
	
		// draw a small circle for each outward facing in and out port
		dot.shape("circle").width(0.1).fillcolor("#FFFFFF");
		for (Port p : workflow.inPorts) dot.node(p.comment.binding(), "");
        for (Port p : workflow.outPorts) dot.node(p.comment.binding(), "");
		
		for (Channel c : workflow.channels) {
		    
		    Program sourceProgram = c.sourceProgram;
		    Program sinkProgram = c.sinkProgram;
		    
		    // draw edges for channels between workflow in ports and programs in workflow
		    if (sourceProgram == null) {
		        
                dot.edge(c.sinkPort.comment.binding(),
                         c.sinkProgram.beginComment.programName,
                         c.sinkPort.comment.binding());
		        
            // draw edges for channels between programs in workflow and workflow out ports
		    } else if (sinkProgram == null) {
		        
                dot.edge(c.sourceProgram.beginComment.programName,
                         c.sourcePort.comment.binding(),
                         c.sourcePort.comment.binding());
		        
            // draw edges for channels between programs within workflow
		    } else {
		    
    			dot.edge(c.sourceProgram.beginComment.programName,
    			         c.sinkProgram.beginComment.programName,
    			         c.sourcePort.comment.binding());
		    }
		}
				
		dot.end();
		
		return dot.toString();
	}
}

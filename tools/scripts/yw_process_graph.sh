#!/usr/bin/env bash -l
#
# ./run_queries.sh &> run_queries.txt

source $SCRIPT_SETTINGS_FILE

xsb --quietload --noprompt --nofeedback --nobanner << END_XSB_STDIN

set_prolog_flag(unknown, fail).

['$FACTS_DIR/yw_views'].
['$RULES_DIR/yw_rules'].
['$RULES_DIR/gv_rules'].
['$RULES_DIR/yw_graph_rules'].

[user].
graph :-

    yw_workflow_script(W, WorkflowName, _, _),

    gv_graph('yw_data_view', WorkflowName, 'LR'),

        gv_cluster('workflow', 'black'),
            gv_nodestyle__atomic_step,
            gv_nodes__atomic_steps(W),
            gv_nodestyle__subworkflow,
            gv_nodes__subworkflows(W),
        gv_cluster_end,

        gv_cluster('inflows', 'white'),
            gv_node_style__workflow_port,
            gv_nodes__inflows(W),
        gv_cluster_end,

        gv_cluster('outflows', 'white'),
            gv_node_style__workflow_port,
            gv_nodes__outflows(W),
        gv_cluster_end,

        gv_edges__step_to_step(W),
        gv_edges__inflow_to_step(W),
        gv_edges__step_to_outflow(W),

    gv_graph_end.

end_of_file.

graph.

END_XSB_STDIN

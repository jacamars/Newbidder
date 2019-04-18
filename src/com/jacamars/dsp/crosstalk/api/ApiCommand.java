package com.jacamars.dsp.crosstalk.api;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacamars.dsp.crosstalk.budget.CommandController;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.commands.BasicCommand;
import com.jacamars.dsp.rtb.tools.XORShiftRandom;

/**
 * A class that implements a JSON version of the RTB4FREE ZeroMQ command structure.
 *
 * @author Ben M. Faul
 */

public class ApiCommand {
    /**
     * Type is Get status
     */
    public static final String GetStatus = "GetStatus#";
    /**
     * Type is Start the bidder
     */
    public static final String StartBidder = "StartBidder#";
    /**
     * Type is Stop the bidder
     */
    public static final String StopBidder = "StopBidder#";
    /**
     * Type is Refresh from SQL
     */
    public static final String Refresh = "Refresh#";
    /**
     * Type is Ping crosstalk
     */
    public static final String Ping = "Ping#";
    /**
     * Type is Get Price from Bidder
     */
    public static final String GetPrice = "GetPrice#";
    /**
     * Set the price
     */
    public static final String SetPrice = "SetPrice#";
    /**
     * Get the budget
     */
    public static final String GetBudget = "GetBudget#";
    /**
     * Set the budget
     */
    public static final String SetBudget = "SetBudget#";
    /**
     * Get the actual cost values
     */
    public static final String GetValues = "GetValues#";
    /**
     * Update a campaign in the bidders
     */
    public static final String Update = "Update#";
    /**
     * Delete a campaign from the bidder
     */
    public static final String Delete = "Delete#";
    /**
     * Get the JSON representation of a campaign
     */
    public static final String GetJson = "GetJson#";
    /**
     * Return the future results from an async command
     */
    public static final String Future = "Future#";
    /**
     * Get a campaign definition from SQL
     */
    public static final String GetCampaign = "GetCampaign#";
    /**
     * Get reason not loaded into the bidders
     */
    public static final String GetReason = "GetReason#";
    /**
     * Add a campaign back into the bidders
     */
    public static final String Add = "AddCampaign#";
    /**
     * List campaigns in the bidders
     */
    public static final String ListCampaigns = "ListCampaigns#";
    /**
     * Configure AWS command
     */
    public static final String ConfigureAws = "ConfigureAws#";
    /**
     * The unbkown command, is returned if Web API can't figure out what to do
     */
    public static final String Unknown = "Unknown#";
    /**
     * Get the spend rate of a campaign or campaign/creative
     */
    public static final String SpendRate = "GetSpendRate#";
    /**
     * Dump heap
     */
    public static final String Dump = "Dump#";
    /**
     * Get Bidders Status
     */
    public static final String GetBiddersStatus = "GetBiddersStatus#";
    /**
     * Set the weights on a campaign
     */
    public static final String SetWeights = "SetWeights#";
    /**
     * Get the weights on a campaign
     */
    public static final String GetWeights = "GetWeights#";

    /**
     * This class'es sl4j log object
     */
    static final Logger logger = LoggerFactory.getLogger(ApiCommand.class);

    /**
     * Indicates if the response was an error
     */
    public Boolean error = false;

    /**
     * Contains nny message to return to user
     */
    public String message;

    /**
     * The current time
     */
    public long timestamp = System.currentTimeMillis();

    /**
     * Indicates this is an asnc command
     */
    public Boolean async = null;
    /**
     * The async id
     */
    public String asyncid = null;

    /**
     * An object For serialization we use an object mapper
     */
    protected static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setSerializationInclusion(Include.NON_NULL);
    }

    /**
     * List of bidders that respond
     */
    public List<RefreshList> refreshList = null;

    /**
     * A place for command response call back messages on future's
     */
    protected static ConcurrentHashMap<String,BasicCommand> responses = new ConcurrentHashMap();
    /**
     * The future results
     */
    protected static volatile Map<String, BasicCommand> futures = new HashMap<String, BasicCommand>();
    /**
     * Fast random numbers
     */
    static volatile XORShiftRandom random = new XORShiftRandom();

    /**
     * Get the username.
     *
     * @return String. The user name.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the user name
     *
     * @param username String.The name to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get the passeord.
     *
     * @return String. The password used.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password.
     *
     * @param password String. The password to use.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public String getBidder() {
        return bidder;
    }

    public void setBidder(String bidder) {
        this.bidder = bidder;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public void setCreative(String creative) {
        this.creative = creative;
    }

    public String getCreative() {
        return creative;
    }

    // The command user name
    protected String username;
    // The command password
    protected String password;
    // The command bidder we targer, or null for all bidders
    protected String bidder;
    // The type of the command.
    protected String type;
    // The campaign target
    protected String campaign;
    // The creative target
    protected String creative;

    /**
     * Empty constructor for JSON
     */
    public ApiCommand() {

    }

    /**
     * Command form using username and password.
     *
     * @param username String. The username to use.
     * @param password String. The password to use.
     */
    public ApiCommand(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Instantiate an API command. Indirectly executes the action of the command.
     *
     * @param ip   String. The IP address where the command originated from.
     * @param data String. The data of the POST.
     * @return ApiCommand. The command object, after execution (and response from the bidders).
     * @throws Exception on network or JSON errors.
     */
    public static ApiCommand instantiate(String ip, String data) throws Exception {
    	
        int i = data.indexOf("\"type");
        int j = data.substring(i).indexOf("#");
        String token = data.substring(i + 8, i + j + 1);
        token = token.replaceAll("\"", "");
        
        ApiCommand cmd = null;

        logger.info("From IP: {}, command: {}", ip, data);
        switch (token) {
            case Ping:
                cmd = mapper.readValue(data, PingCmd.class);
                break;
            case GetPrice:
                cmd = mapper.readValue(data, GetPriceCmd.class);
                break;
            case SetPrice:
                cmd = mapper.readValue(data, SetPriceCmd.class);
                break;
            case GetBudget:
                cmd = mapper.readValue(data, GetBudgetCmd.class);
                break;
            case SetBudget:
                cmd = mapper.readValue(data, SetBudgetCmd.class);
                break;
            case GetValues:
                cmd = mapper.readValue(data, GetValuesCmd.class);
                break;
            case Update:
                cmd = mapper.readValue(data, UpdateCmd.class);
                break;
            case Delete:
                cmd = mapper.readValue(data, DeleteCmd.class);
                break;
            case GetCampaign:
                cmd = mapper.readValue(data, GetCampaignCmd.class);
                break;

            case ListCampaigns:
                cmd = mapper.readValue(data, ListCampaignsCmd.class);
                break;

            case GetReason:
                cmd = mapper.readValue(data, GetReasonCmd.class);
                break;

            case Add:
                cmd = mapper.readValue(data, AddCampaignCmd.class);
                break;

            case ConfigureAws:
                cmd = mapper.readValue(data, ConfigureAwsObjectCmd.class);
                break;

            case Future:
                cmd = mapper.readValue(data, FutureCmd.class);
                break;

            case SpendRate:
                cmd = mapper.readValue(data, GetSpendRateCmd.class);
                break;

            case Dump:
                cmd= mapper.readValue(data, DumpCmd.class);
                break;

            case GetBiddersStatus:
                cmd = mapper.readValue(data, GetBiddersStatusCmd.class);
                break;

            case SetWeights:
                cmd = mapper.readValue(data, SetWeightsCmd.class);
                break;
            case GetWeights:
                cmd = mapper.readValue(data, GetWeightsCmd.class);
                break;
                
            case Refresh:
            	cmd = mapper.readValue(data, RefreshCmd.class);
            	break;

            default:
                cmd = new UnknownCmd(token);
                return cmd;
        }
        
        if (!RTBServer.isLeader()) {
        	cmd  = CommandController.getInstance().sendCommand(cmd, 45000);
        } else {
        	cmd.execute();
        }
        
        if (cmd instanceof FutureCmd) {
        	cmd = ((FutureCmd) cmd).cmd;
        }
        return cmd;
    }

    /**
     * Please override me.
     *
     * @return null.
     * @throws Exception on JSON serialization errors.
     */
    public String toJson() throws Exception {
        return null;
    }

    /**
     * Please override me. This executes the ZeroMQ command and marshalls the response. Each
     * command implements this as needed.
     */
    public void execute() {
        this.timestamp = System.currentTimeMillis();
        username = null;
        password = null;
    }

    /**
     * Got a callback from ZeroMQ destined for the API.
     *
     * @param cmd BasicCommand. The command to send to the bidders.
     */
    public static void callBack(BasicCommand cmd) {
        String key = cmd.id;
        responses.put(key, cmd);
    }

    public static void addFuture(BasicCommand cmd) {
        String key = cmd.id;
        futures.put(key,cmd);
    }

}
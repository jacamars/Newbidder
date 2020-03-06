package com.jacamars.dsp.crosstalk.api;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacamars.dsp.crosstalk.budget.CommandController;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.commands.BasicCommand;
import com.jacamars.dsp.rtb.shared.BidCachePool;
import com.jacamars.dsp.rtb.shared.TokenData;
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
    
    public static final String GetAccounting = "GetAccounting#";
    
    public static final String  SQLLIST_CAMPAIGNS = "SQLListCampaigns#";
    
    public static final String SQLGET_NEW_CAMPAIGN = "SQLGetNewCampaign#";
    
    public static final String SQLGET_NEW_TARGET = "SQLGetNewTarget#";
    
    public static final String SQLGET_NEW_CREATIVE = "SQLGetNewCreative#";
    
    public static final String SQLGET_NEW_RULE = "SQLGetNewRule#";
    
    public static final String SQLLIST_CREATIVES = "SQLListCreatives#";
    
    public static final String SQLADD_NEW_CAMPAIGN= "SQLAddNewCampaign#";
    
    public static final String SQLDELETE_CAMPAIGN= "SQLDeleteCampaign#";
    
    public static final String SQLGET_CAMPAIGN= "SQLGetCampaign#";
    
    public static final String SQLADD_NEW_RULE= "SQLAddNewRule#";
    
    public static final String SQLLIST_RULES= "SQLListRules#";
    
    public static final String SQLGET_RULE = "SQLGetRule#";
    
    public static final String SQLDELETE_RULE = "SQLDeleteRule#";
    
    public static final String SQLADD_NEW_TARGET = "SQLAddNewTarget#";
    
    public static final String SQLDELETE_TARGET = "SQLDeleteTarget#";
    
    public static final String SQLLIST_TARGETS = "SQLListTargets#";
    
    public static final String SQLGET_TARGET = "SQLGetTarget#";
    
    public static final String SQLADD_NEW_CREATIVE = "SQLAddNewCreative#";
    
    public static final String SQLGET_CREATIVE = "SQLGetCreative#";
    
    public static final String SQLDELETE_CREATIVE = "SQLDeleteCreative#";
    
    public static final String MACROSUB = "MacroSub#";
    
    public static final String LIST_BIGDATA = "ListBigData#";
    
    public static final String DELETE_SYMBOL = "DeleteSymbol#";
    
    public static final String QUERY_SYMBOL = "QuerySymbol#";
    
    public static final String LIST_MACROS = "ListMacros#";
    
    public static final String GET_TOKEN = "GetToken#";
    
    public static final String GET_USER = "SQLGetUser#";
    
    public static final String SET_USER = "SQLAddNewUser#";
   
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
     * JWT
     */
    public String token = null;
    
    /**
     * An object For serialization we use an object mapper
     */
    protected static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setSerializationInclusion(Include.NON_NULL);
    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

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
    // The command bidder we targer, or null for all bidders
    protected String bidder;
    // The type of the command.
    protected String type;
    // The campaign target
    protected String campaign;
    // The creative target
    protected String creative;
    
    protected TokenData tokenData;

    /**
     * Empty constructor for JSON
     */
    public ApiCommand() {

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
        
        boolean requireLeader = false;
        
        logger.info("From IP: {}, size: {}, command: {}", ip, data.length(), data);
        switch (token) {
            case Ping:
                cmd = mapper.readValue(data, PingCmd.class);
                break;
            case GetPrice:
                cmd = mapper.readValue(data, GetPriceCmd.class);
                break;
            case SetPrice:
                cmd = mapper.readValue(data, SetPriceCmd.class);
                requireLeader = true;
                break;
            case GetBudget:
                cmd = mapper.readValue(data, GetBudgetCmd.class);
                break;
            case SetBudget:
                cmd = mapper.readValue(data, SetBudgetCmd.class);
                requireLeader = true;
                break;
            case GetValues:
                cmd = mapper.readValue(data, GetValuesCmd.class);
                break;
        
            case Delete:
                cmd = mapper.readValue(data, DeleteCmd.class);
                requireLeader = true;
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
                requireLeader = true;
                break;

            case ConfigureAws:
                cmd = mapper.readValue(data, ConfigureAwsObjectCmd.class);
                requireLeader = true;
                break;

            case Future:
                cmd = mapper.readValue(data, FutureCmd.class);
                break;

            case SpendRate:
                cmd = mapper.readValue(data, GetSpendRateCmd.class);
                break;

            case GetBiddersStatus:
                cmd = mapper.readValue(data, GetBiddersStatusCmd.class);
                break;

            case SetWeights:
                cmd = mapper.readValue(data, SetWeightsCmd.class);
                requireLeader = true;
                break;
            case GetWeights:
                cmd = mapper.readValue(data, GetWeightsCmd.class);
                break;
                
            case Refresh:
            	cmd = mapper.readValue(data, RefreshCmd.class);
            	requireLeader = true;
            	break;
            	
            case GetAccounting:
            	cmd = mapper.readValue(data,  GetAccountingCmd.class);
            	break;
            	
            case SQLLIST_CAMPAIGNS:
            	cmd = mapper.readValue(data, SQLListCampaigns.class);
            	break;
            	
            case SQLGET_NEW_CAMPAIGN:
            	cmd = mapper.readValue(data, SQLGetNewCampaignCmd.class);
            	break;
            	
            case SQLGET_NEW_TARGET:
            	cmd = mapper.readValue(data, SQLGetNewTargetCmd.class);
            	break;
            	
            case SQLGET_NEW_RULE:
            	cmd = mapper.readValue(data, SQLGetNewRuleCmd.class);
            	break;
            	
            case SQLLIST_CREATIVES:
            	cmd = mapper.readValue(data, SQLListCreatives.class);
            	break;
            	
            case SQLGET_NEW_CREATIVE:
            	cmd = mapper.readValue(data, SQLGetNewCreativeCmd.class);
            	break;
            	
            case SQLADD_NEW_CAMPAIGN:
            	cmd = mapper.readValue(data, SQLAddNewCampaignCmd.class);
            	break;
            	
            case SQLDELETE_CAMPAIGN:
            	cmd = mapper.readValue(data, SQLDeleteCampaignCmd.class);
            	requireLeader = true;
            	break;
            	
            case SQLGET_CAMPAIGN:
            	cmd = mapper.readValue(data, SQLGetCampaignCmd.class);
            	break;
            	
            case SQLADD_NEW_RULE:
            	cmd = mapper.readValue(data, SQLAddNewRuleCmd.class);
            	requireLeader = true;
            	break;
            	
            case SQLLIST_RULES:
            	cmd = mapper.readValue(data, SQLListRulesCmd.class);
            	break;
            	
            case SQLGET_RULE:
            	cmd = mapper.readValue(data, SQLGetRuleCmd.class);
            	break;
            	
            case SQLDELETE_RULE:
            	cmd = mapper.readValue(data, SQLDeleteRuleCmd.class);
            	requireLeader = true;
            	break;
            	
            case SQLADD_NEW_TARGET:
            	cmd = mapper.readValue(data, SQLAddNewTargetCmd.class);
            	requireLeader = true;
            	break;
            	
            case SQLDELETE_TARGET:
            	cmd = mapper.readValue(data, SQLDeleteTargetCmd.class);
            	requireLeader = true;
            	break;
            	
            case SQLLIST_TARGETS:
            	cmd = mapper.readValue(data, SQLListTargetsCmd.class);
            	break;
            	
            case SQLGET_TARGET:
            	cmd = mapper.readValue(data, SQLGetTargetCmd.class);
            	break;
            	
            case SQLADD_NEW_CREATIVE:
            	cmd = mapper.readValue(data, SQLAddNewCreativeCmd.class);
            	requireLeader = true;
            	break;
            	
            case SQLGET_CREATIVE:
            	cmd = mapper.readValue(data, SQLGetCreativeCmd.class);
            	break;
            	
            case SQLDELETE_CREATIVE:
            	cmd = mapper.readValue(data, SQLDeleteCreativeCmd.class);
            	requireLeader = true;
            	break;
            	
            case MACROSUB:
            	cmd = mapper.readValue(data, MacroSubCmd.class);
            	break;
            	
            case LIST_BIGDATA:
            	cmd = mapper.readValue(data, ListBigDataCmd.class);
            	break;
            	
            case DELETE_SYMBOL:
            	cmd = mapper.readValue(data, DeleteSymbolCmd.class);
            	requireLeader = true;
            	break;
            	
            case QUERY_SYMBOL:
            	cmd = mapper.readValue(data, QuerySymbolCmd.class);
            	requireLeader = true;
            	break;
            	
            case LIST_MACROS:
            	cmd = mapper.readValue(data, ListMacrosCmd.class);
            	requireLeader = true;
            	break;
            	
            case GET_TOKEN:
            	cmd = mapper.readValue(data, GetTokenCmd.class);
            	break;
            	
            case GET_USER:
            	cmd = mapper.readValue(data, SQLGetUserCmd.class);
            	break;
            	
            case SET_USER:
            	cmd = mapper.readValue(data, SQLAddNewUserCmd.class);
            	break;
            	
            	
            default:
                cmd = new UnknownCmd(token);
                cmd.error = true;
                cmd.message = "Unkown command: " + token;
                return cmd;
        }
        
        /**
         * Retrieve the token
         */
        if (token.equals(GET_TOKEN) == false) {
        	if (cmd.token == null) {
        		cmd.error = true;
        		cmd.message = "You did not supply a token (" + token + ")";
        		return cmd;
        	}
        	cmd.tokenData = BidCachePool.getInstance().getToken(cmd.token);
        	if (cmd.tokenData == null) {
        		cmd.error = true;
        		cmd.message = "Token expired";
        		return cmd;
        	}
        }
        
        ///////////// If this is not the leader, but leadership is required, then send it to the leader ///////////
        if (requireLeader) {
        	if (!RTBServer.isLeader()) 
        		cmd  = CommandController.getInstance().sendCommand(cmd, 45000);
        	else 
        		cmd.execute();
        } else {
        	cmd.execute();
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
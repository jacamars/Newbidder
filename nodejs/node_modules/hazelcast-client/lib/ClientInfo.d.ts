import Address = require('./Address');
export declare class ClientInfo {
    /**
     * Unique id of this client instance. It is provided by owner server instance.
     */
    uuid: string;
    /**
     * Local port address that is used to communicate with cluster.
     */
    localAddress: Address;
    /**
     * Type of this client. It is always NodeJS.
     */
    type: string;
}

import Address = require('../Address');
export declare class Member {
    /**
     * Network address of member.
     */
    address: Address;
    /**
     * Unique id of member in cluster.
     */
    uuid: string;
    /**
     * true if member is a lite member.
     */
    isLiteMember: boolean;
    attributes: {
        [id: string]: string;
    };
    constructor(address: Address, uuid: string, isLiteMember?: boolean, attributes?: {
        [id: string]: string;
    });
    equals(other: Member): boolean;
    toString(): string;
}

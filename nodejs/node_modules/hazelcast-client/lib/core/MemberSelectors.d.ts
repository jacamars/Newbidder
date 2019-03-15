import { Member } from './Member';
import { MemberSelector } from './MemberSelector';
export declare class DataMemberSelector implements MemberSelector {
    select(member: Member): boolean;
}
export declare class MemberSelectors {
    static readonly DATA_MEMBER_SELECTOR: DataMemberSelector;
}

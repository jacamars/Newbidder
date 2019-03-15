import ClientMessage = require('../ClientMessage');
import { StackTraceElementCodec } from './StackTraceElementCodec';
export declare class ErrorCodec {
    errorCode: number;
    className: string;
    message: string;
    stackTrace: StackTraceElementCodec[];
    causeErrorCode: number;
    causeClassName: string;
    static decode(clientMessage: ClientMessage): ErrorCodec;
}

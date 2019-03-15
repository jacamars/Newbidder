import ClientMessage = require('../ClientMessage');
export declare class StackTraceElementCodec {
    declaringClass: string;
    methodName: string;
    fileName: string;
    lineNumber: number;
    static decode(payload: ClientMessage): StackTraceElementCodec;
}

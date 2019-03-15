import ClientMessage = require('../ClientMessage');
export declare class ClientErrorFactory {
    private codeToErrorConstructor;
    constructor();
    createErrorFromClientMessage(clientMessage: ClientMessage): Error;
    createError(errorCode: number, className: string, message: string, cause: Error): Error;
    private register(code, errorFactory);
}

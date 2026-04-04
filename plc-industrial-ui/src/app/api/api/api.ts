export * from './plcHistoryController.service';
import { PlcHistoryControllerService } from './plcHistoryController.service';
export * from './plcHistoryController.serviceInterface';
export * from './plcQueryController.service';
import { PlcQueryControllerService } from './plcQueryController.service';
export * from './plcQueryController.serviceInterface';
export const APIS = [PlcHistoryControllerService, PlcQueryControllerService];

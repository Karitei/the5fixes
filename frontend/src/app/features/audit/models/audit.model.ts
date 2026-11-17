
export enum JobStatus {
  PENDING = 'PENDING',
  RUNNING = 'RUNNING',
  COMPLETED = 'DONE',
  FAILED = 'FAILED'
}

export interface AuditResponse{
  jobId: string;
  status: JobStatus;
}

                             
export enum Severity{
  CRITICAL ='CRITICAL',
  HIGH = 'HIGH' ,  
  MEDIUM = 'MEDIUM',
  LOW = 'LOW'
}     

export interface AuditScores{
performance : number,
accessibility : number,
bestPractices : number,
seo : number,
loadTimeSeconds : number,
pageSize: number
}

export interface FindingResponse{
  severity : Severity,
  title : string,
  lcpSavingsMs : number,
  fcpSavingsMs : number, 
  effortMinutes: number
}

export interface AuditStatusResponse{
  status : JobStatus,
  scores : AuditScores,
  auditFindings : FindingResponse[],
  errorMessage : string
}


 

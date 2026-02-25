import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export type CollaborationType = 'ONE_TIME' | 'RECURRING' | 'LONG_TERM';
export type CollaborationStatus = 'OPEN' | 'MATCHED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED' | 'ON_HOLD' | 'ARCHIVED';

export interface Collaboration {
  id?: number;
  companyId: number;
  title: string;
  description: string;
  collaborationType: CollaborationType;
  requiredSkills: string;
  budgetMin: number;
  budgetMax: number;
  estimatedDuration: string;
  complexityLevel: string;
  deadline: string;
  confidentialityOption: boolean;
  maxFreelancersNeeded?: number;
  milestoneStructure?: string;
  attachments?: string;
  industry?: string;
  status?: CollaborationStatus;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateCollaborationRequest {
  companyId: number;
  title: string;
  description: string;
  collaborationType: CollaborationType;
  requiredSkills: string;
  budgetMin: number;
  budgetMax: number;
  estimatedDuration: string;
  complexityLevel: string;
  deadline: string;
  confidentialityOption: boolean;
  maxFreelancersNeeded?: number;
  milestoneStructure?: string;
  attachments?: string;
  industry?: string;
}

export interface UpdateCollaborationRequest {
  title?: string;
  description?: string;
  collaborationType?: CollaborationType;
  requiredSkills?: string;
  budgetMin?: number;
  budgetMax?: number;
  estimatedDuration?: string;
  complexityLevel?: string;
  deadline?: string;
  confidentialityOption?: boolean;
  maxFreelancersNeeded?: number;
  milestoneStructure?: string;
  attachments?: string;
  industry?: string;
  status?: CollaborationStatus;
}

@Injectable({ providedIn: 'root' })
export class CollaborationService {
  private apiUrl = 'http://localhost:8082/api/collaborations';

  constructor(private http: HttpClient) {}

  create(request: CreateCollaborationRequest): Observable<Collaboration> {
    return this.http.post<Collaboration>(this.apiUrl, request);
  }

  getById(id: number): Observable<Collaboration> {
    return this.http.get<Collaboration>(`${this.apiUrl}/${id}`);
  }

  /** Get ALL collaborations with no filter (for admin dashboard). Use when admin has "All companies" + "All statuses". */
  getAllForAdmin(): Observable<Collaboration[]> {
    return this.http.get<Collaboration[]>(this.apiUrl);
  }

  getAll(params?: {
    companyId?: number;
    status?: CollaborationStatus;
    skills?: string;
    budgetMin?: number;
    budgetMax?: number;
    estimatedDuration?: string;
    industry?: string;
  }): Observable<Collaboration[]> {
    let httpParams = new HttpParams();
    if (params?.companyId != null) httpParams = httpParams.set('companyId', params.companyId);
    if (params?.status != null) httpParams = httpParams.set('status', params.status);
    if (params?.skills != null && params.skills !== '') httpParams = httpParams.set('skills', params.skills);
    if (params?.budgetMin != null) httpParams = httpParams.set('budgetMin', params.budgetMin.toString());
    if (params?.budgetMax != null) httpParams = httpParams.set('budgetMax', params.budgetMax.toString());
    if (params?.estimatedDuration != null && params.estimatedDuration !== '') httpParams = httpParams.set('estimatedDuration', params.estimatedDuration);
    if (params?.industry != null && params.industry !== '') httpParams = httpParams.set('industry', params.industry);
    return this.http.get<Collaboration[]>(this.apiUrl, { params: httpParams });
  }

  update(id: number, companyId: number, request: UpdateCollaborationRequest, adminOverride = false): Observable<Collaboration> {
    return this.http.put<Collaboration>(`${this.apiUrl}/${id}`, request, {
      params: { companyId: companyId.toString(), adminOverride: adminOverride.toString() }
    });
  }

  updateStatus(id: number, status: CollaborationStatus, companyId: number, adminOverride = false): Observable<Collaboration> {
    return this.http.patch<Collaboration>(`${this.apiUrl}/${id}/status`, null, {
      params: { status, companyId: companyId.toString(), adminOverride: adminOverride.toString() }
    });
  }

  delete(id: number, companyId: number, adminOverride = false): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, {
      params: { companyId: companyId.toString(), adminOverride: adminOverride.toString() }
    });
  }
}

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export type CollaborationRequestStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED';
export type NegotiationStatus = 'INITIAL' | 'NEGOTIATING' | 'COUNTER_OFFERED' | 'AGREED' | 'DECLINED';

export interface CollaborationRequestDto {
  id?: number;
  collaborationId: number;
  freelancerId: number;
  proposalMessage?: string;
  proposedPrice?: number;
  status: CollaborationRequestStatus;
  createdAt?: string;
  updatedAt?: string;
  
  // Negotiation fields
  negotiationStatus?: NegotiationStatus;
  counterOfferPrice?: number;
  counterOfferTimeline?: string;
  counterOfferMessage?: string;
  counterOfferedBy?: number;
  counterOfferedAt?: string;
  counterOfferCount?: number;
  proposedMilestones?: any[];
  agreedMilestones?: any[];
  freelancerAgreed?: boolean;
  freelancerAgreedAt?: string;
  companyAgreed?: boolean;
  companyAgreedAt?: string;
  finalAgreedPrice?: number;
  finalAgreedTimeline?: string;
}

export interface CreateCollaborationRequestDto {
  collaborationId: number;
  freelancerId: number;
  proposalMessage?: string;
  proposedPrice?: number;
}

export interface UpdateProposalDto {
  proposalMessage?: string;
  proposedPrice?: number;
}

@Injectable({ providedIn: 'root' })
export class CollaborationRequestService {
  private apiUrl = 'http://localhost:8082/api/collaboration-requests';

  constructor(private http: HttpClient) {}

  create(dto: CreateCollaborationRequestDto): Observable<CollaborationRequestDto> {
    return this.http.post<CollaborationRequestDto>(this.apiUrl, dto);
  }

  getById(id: number): Observable<CollaborationRequestDto> {
    return this.http.get<CollaborationRequestDto>(`${this.apiUrl}/${id}`);
  }

  getByCollaborationId(collaborationId: number): Observable<CollaborationRequestDto[]> {
    return this.http.get<CollaborationRequestDto[]>(`${this.apiUrl}/collaboration/${collaborationId}`);
  }

  getByFreelancerId(freelancerId: number): Observable<CollaborationRequestDto[]> {
    return this.http.get<CollaborationRequestDto[]>(`${this.apiUrl}/freelancer/${freelancerId}`);
  }

  /** Get all applications (admin). Optional filters: collaborationId, freelancerId. No params = all. */
  getAll(params?: { collaborationId?: number; freelancerId?: number }): Observable<CollaborationRequestDto[]> {
    let httpParams = new HttpParams();
    if (params?.collaborationId != null) httpParams = httpParams.set('collaborationId', params.collaborationId);
    if (params?.freelancerId != null) httpParams = httpParams.set('freelancerId', params.freelancerId);
    return this.http.get<CollaborationRequestDto[]>(this.apiUrl, { params: httpParams });
  }

  updateStatus(id: number, companyId: number, status: CollaborationRequestStatus): Observable<CollaborationRequestDto> {
    return this.http.patch<CollaborationRequestDto>(`${this.apiUrl}/${id}/status`, { status }, {
      params: { companyId: companyId.toString() }
    });
  }

  withdraw(id: number, freelancerId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}/withdraw`, {
      params: { freelancerId: freelancerId.toString() }
    });
  }

  updateProposal(id: number, freelancerId: number, body: UpdateProposalDto): Observable<CollaborationRequestDto> {
    return this.http.patch<CollaborationRequestDto>(`${this.apiUrl}/${id}`, body, {
      params: { freelancerId: freelancerId.toString() }
    });
  }
}

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface NegotiationMessage {
  id?: number;
  collaborationRequestId: number;
  senderId: number;
  senderType: 'FREELANCER' | 'COMPANY';
  messageType: 'TEXT' | 'COUNTER_OFFER' | 'MILESTONE_PROPOSAL' | 'QUESTION' | 'ANSWER' | 'SYSTEM';
  message: string;
  metadata?: any;
  isRead?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface CounterOfferRequest {
  collaborationRequestId: number;
  counterOfferedBy: number;
  counterOfferedByType: 'FREELANCER' | 'COMPANY';
  counterOfferPrice: number;
  counterOfferTimeline: string;
  counterOfferMessage: string;
  proposedMilestones?: MilestoneProposal[];
}

export interface MilestoneProposal {
  name: string;
  percentage: number;
  deliverables: string;
  duration: string;
}

export interface AgreeToTermsRequest {
  collaborationRequestId: number;
  userId: number;
  userType: 'FREELANCER' | 'COMPANY';
  agreed: boolean;
}

export interface ProposalRevision {
  id: number;
  collaborationRequestId: number;
  revisionNumber: number;
  revisedBy: number;
  revisedByType: 'FREELANCER' | 'COMPANY';
  previousPrice?: number;
  newPrice?: number;
  previousTimeline?: string;
  newTimeline?: string;
  previousMilestones?: any[];
  newMilestones?: any[];
  revisionMessage?: string;
  revisionReason?: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class NegotiationService {
  private apiUrl = `${environment.collaborationServiceUrl}/api/negotiations`;

  constructor(private http: HttpClient) {}

  /**
   * Send a negotiation message
   */
  sendMessage(request: NegotiationMessage): Observable<NegotiationMessage> {
    return this.http.post<NegotiationMessage>(`${this.apiUrl}/messages`, request);
  }

  /**
   * Get all messages for a collaboration request
   */
  getMessages(collaborationRequestId: number): Observable<NegotiationMessage[]> {
    return this.http.get<NegotiationMessage[]>(`${this.apiUrl}/${collaborationRequestId}/messages`);
  }

  /**
   * Get unread message count
   */
  getUnreadCount(collaborationRequestId: number): Observable<{ unreadCount: number }> {
    return this.http.get<{ unreadCount: number }>(`${this.apiUrl}/${collaborationRequestId}/unread-count`);
  }

  /**
   * Mark messages as read
   */
  markAsRead(collaborationRequestId: number, userId: number): Observable<any> {
    const params = new HttpParams().set('userId', userId.toString());
    return this.http.post(`${this.apiUrl}/${collaborationRequestId}/mark-read`, null, { params });
  }

  /**
   * Send a counter-offer
   */
  sendCounterOffer(request: CounterOfferRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/counter-offer`, request);
  }

  /**
   * Agree to negotiated terms
   */
  agreeToTerms(request: AgreeToTermsRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/agree`, request);
  }

  /**
   * Get revision history
   */
  getRevisionHistory(collaborationRequestId: number): Observable<ProposalRevision[]> {
    return this.http.get<ProposalRevision[]>(`${this.apiUrl}/${collaborationRequestId}/revisions`);
  }
}

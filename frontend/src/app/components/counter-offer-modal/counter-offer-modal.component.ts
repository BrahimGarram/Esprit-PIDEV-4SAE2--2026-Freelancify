import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { NegotiationService, CounterOfferRequest, MilestoneProposal } from '../../services/negotiation.service';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-counter-offer-modal',
  templateUrl: './counter-offer-modal.component.html',
  styleUrls: ['./counter-offer-modal.component.css']
})
export class CounterOfferModalComponent implements OnInit {
  @Input() collaborationRequestId!: number;
  @Input() currentUserId!: number;
  @Input() userType!: 'FREELANCER' | 'COMPANY';
  @Input() originalPrice?: number;
  @Input() originalTimeline?: string;
  @Input() currentPrice?: number;
  @Input() currentTimeline?: string;
  @Input() existingMilestones?: any[];
  
  @Output() counterOfferSent = new EventEmitter<any>();
  @Output() closed = new EventEmitter<void>();
  
  // Expose Math to template
  Math = Math;
  
  counterOfferPrice: number = 0;
  counterOfferTimeline: string = '';
  counterOfferMessage: string = '';
  milestones: MilestoneProposal[] = [];
  
  isSending = false;
  showMilestones = false;
  
  constructor(
    private negotiationService: NegotiationService,
    private toast: ToastService
  ) {}
  
  ngOnInit() {
    // Initialize with current values or original values
    this.counterOfferPrice = this.currentPrice || this.originalPrice || 0;
    this.counterOfferTimeline = this.currentTimeline || this.originalTimeline || '';
    
    // Initialize milestones if they exist
    if (this.existingMilestones && this.existingMilestones.length > 0) {
      this.milestones = this.existingMilestones.map(m => ({
        name: m.name || '',
        percentage: m.percentage || 0,
        deliverables: m.deliverables || '',
        duration: m.duration || ''
      }));
      this.showMilestones = true;
    } else {
      // Default: 3 milestones
      this.milestones = [
        { name: 'Phase 1', percentage: 30, deliverables: '', duration: '' },
        { name: 'Phase 2', percentage: 40, deliverables: '', duration: '' },
        { name: 'Phase 3', percentage: 30, deliverables: '', duration: '' }
      ];
    }
  }
  
  get totalPercentage(): number {
    return this.milestones.reduce((sum, m) => sum + (m.percentage || 0), 0);
  }
  
  get isValid(): boolean {
    if (!this.counterOfferPrice || this.counterOfferPrice <= 0) return false;
    if (!this.counterOfferTimeline?.trim()) return false;
    if (!this.counterOfferMessage?.trim()) return false;
    
    if (this.showMilestones) {
      if (this.milestones.length === 0) return false;
      if (this.totalPercentage !== 100) return false;
      
      for (const m of this.milestones) {
        if (!m.name?.trim() || !m.percentage || m.percentage <= 0) return false;
      }
    }
    
    return true;
  }
  
  addMilestone() {
    this.milestones.push({
      name: `Phase ${this.milestones.length + 1}`,
      percentage: 0,
      deliverables: '',
      duration: ''
    });
  }
  
  removeMilestone(index: number) {
    if (this.milestones.length > 1) {
      this.milestones.splice(index, 1);
    }
  }
  
  toggleMilestones() {
    this.showMilestones = !this.showMilestones;
  }
  
  sendCounterOffer() {
    if (!this.isValid) {
      this.toast.error('Please fill in all required fields');
      return;
    }
    
    this.isSending = true;
    
    const request: CounterOfferRequest = {
      collaborationRequestId: this.collaborationRequestId,
      counterOfferedBy: this.currentUserId,
      counterOfferedByType: this.userType,
      counterOfferPrice: this.counterOfferPrice,
      counterOfferTimeline: this.counterOfferTimeline,
      counterOfferMessage: this.counterOfferMessage,
      proposedMilestones: this.showMilestones ? this.milestones : undefined
    };
    
    this.negotiationService.sendCounterOffer(request).subscribe({
      next: (response) => {
        this.toast.success('Counter-offer sent successfully!');
        this.counterOfferSent.emit(response);
        this.isSending = false;
      },
      error: (err) => {
        console.error('Error sending counter-offer', err);
        this.toast.error('Failed to send counter-offer');
        this.isSending = false;
      }
    });
  }
  
  close() {
    this.closed.emit();
  }
  
  getPriceChange(): number {
    const original = this.currentPrice || this.originalPrice || 0;
    return this.counterOfferPrice - original;
  }
  
  getPriceChangePercent(): number {
    const original = this.currentPrice || this.originalPrice || 0;
    if (original === 0) return 0;
    return ((this.counterOfferPrice - original) / original) * 100;
  }
}

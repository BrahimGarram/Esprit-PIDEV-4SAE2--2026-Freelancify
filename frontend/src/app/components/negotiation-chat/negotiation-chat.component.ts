import { Component, Input, Output, EventEmitter, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { NegotiationService, NegotiationMessage } from '../../services/negotiation.service';
import { UserService } from '../../services/user.service';
import { ToastService } from '../../services/toast.service';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-negotiation-chat',
  templateUrl: './negotiation-chat.component.html',
  styleUrls: ['./negotiation-chat.component.css']
})
export class NegotiationChatComponent implements OnInit, OnDestroy {
  @Input() collaborationRequestId!: number;
  @Input() freelancerId!: number;
  @Input() companyId!: number;
  @Input() userType!: 'FREELANCER' | 'COMPANY'; // Current user's type
  
  @Output() counterOfferRequested = new EventEmitter<void>();
  
  @ViewChild('messagesContainer') private messagesContainer!: ElementRef;
  
  messages: NegotiationMessage[] = [];
  newMessage = '';
  currentUserId: number | null = null;
  isLoading = false;
  isSending = false;
  unreadCount = 0;
  
  private pollSubscription?: Subscription;
  
  constructor(
    private negotiationService: NegotiationService,
    private userService: UserService,
    private toast: ToastService
  ) {}
  
  async ngOnInit() {
    await this.loadCurrentUser();
    this.loadMessages();
    this.startPolling();
  }
  
  ngOnDestroy() {
    this.stopPolling();
  }
  
  async loadCurrentUser() {
    try {
      const user = await this.userService.getCurrentUser().toPromise();
      if (user?.id) {
        this.currentUserId = user.id;
      }
    } catch (e) {
      console.error('Error loading current user', e);
    }
  }
  
  loadMessages() {
    if (!this.collaborationRequestId) return;
    
    this.isLoading = true;
    this.negotiationService.getMessages(this.collaborationRequestId).subscribe({
      next: (messages) => {
        this.messages = messages;
        this.isLoading = false;
        this.scrollToBottom();
        
        // Mark messages as read
        if (this.currentUserId) {
          this.negotiationService.markAsRead(this.collaborationRequestId, this.currentUserId).subscribe();
        }
      },
      error: (err) => {
        console.error('Error loading messages', err);
        this.isLoading = false;
        this.toast.error('Failed to load messages');
      }
    });
  }
  
  sendMessage() {
    if (!this.newMessage.trim() || !this.currentUserId) return;
    
    this.isSending = true;
    
    const message: NegotiationMessage = {
      collaborationRequestId: this.collaborationRequestId,
      senderId: this.currentUserId,
      senderType: this.userType,
      messageType: 'TEXT',
      message: this.newMessage.trim()
    };
    
    this.negotiationService.sendMessage(message).subscribe({
      next: (sent) => {
        this.messages.push(sent);
        this.newMessage = '';
        this.isSending = false;
        this.scrollToBottom();
      },
      error: (err) => {
        console.error('Error sending message', err);
        this.isSending = false;
        this.toast.error('Failed to send message');
      }
    });
  }
  
  isOwnMessage(message: NegotiationMessage): boolean {
    return message.senderId === this.currentUserId;
  }
  
  getMessageClass(message: NegotiationMessage): string {
    if (message.messageType === 'SYSTEM') return 'message-system';
    if (message.messageType === 'COUNTER_OFFER') return 'message-counter-offer';
    return this.isOwnMessage(message) ? 'message-own' : 'message-other';
  }
  
  getSenderLabel(message: NegotiationMessage): string {
    if (message.messageType === 'SYSTEM') return 'System';
    if (this.isOwnMessage(message)) return 'You';
    return message.senderType === 'FREELANCER' ? 'Freelancer' : 'Company';
  }
  
  formatTimestamp(timestamp: string | undefined): string {
    if (!timestamp) return '';
    const date = new Date(timestamp);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    
    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins}m ago`;
    if (diffMins < 1440) return `${Math.floor(diffMins / 60)}h ago`;
    
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }
  
  scrollToBottom() {
    setTimeout(() => {
      if (this.messagesContainer) {
        this.messagesContainer.nativeElement.scrollTop = this.messagesContainer.nativeElement.scrollHeight;
      }
    }, 100);
  }
  
  startPolling() {
    // Poll for new messages every 5 seconds
    this.pollSubscription = interval(5000).subscribe(() => {
      if (this.collaborationRequestId && !this.isSending) {
        this.negotiationService.getMessages(this.collaborationRequestId).subscribe({
          next: (messages) => {
            if (messages.length > this.messages.length) {
              this.messages = messages;
              this.scrollToBottom();
              
              // Mark new messages as read
              if (this.currentUserId) {
                this.negotiationService.markAsRead(this.collaborationRequestId, this.currentUserId).subscribe();
              }
            }
          },
          error: () => {}
        });
      }
    });
  }
  
  stopPolling() {
    if (this.pollSubscription) {
      this.pollSubscription.unsubscribe();
    }
  }
  
  onKeyPress(event: KeyboardEvent) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }
  
  openCounterOfferModal() {
    this.counterOfferRequested.emit();
  }
}

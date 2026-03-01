import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { ToastService } from '../../services/toast.service';
import {
  CollaborationService,
  Collaboration,
  CollaborationStatus,
  CreateCollaborationRequest,
  UpdateCollaborationRequest
} from '../../services/collaboration.service';
import { CompanyService, Company } from '../../services/company.service';
import { CollaborationRequestService, CollaborationRequestDto } from '../../services/collaboration-request.service';
import { UserService, User } from '../../services/user.service';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-collaborations',
  templateUrl: './collaborations.component.html',
  styleUrls: ['./collaborations.component.css']
})
export class CollaborationsComponent implements OnInit, OnDestroy {
  collaborations: Collaboration[] = [];
  filteredCollaborations: Collaboration[] = [];
  isLoading = false;
  loadError = false;
  showCreateModal = false;
  showEditModal = false;
  showDetailModal = false;
  showApplyModal = false;
  showCreateCompanyModal = false;
  showNegotiationModal = false;
  showCounterOfferModal = false;
  newCompanyName = '';
  selectedCollaboration: Collaboration | null = null;
  selectedApplicationForNegotiation: CollaborationRequestDto | null = null;
  applications: CollaborationRequestDto[] = [];
  myApplications: CollaborationRequestDto[] = [];
  allApplications: CollaborationRequestDto[] = [];
  allApplicationsLoading = false;
  companies: Company[] = [];
  currentUserId: number | null = null;
  currentUser: User | null = null;
  isAdmin = false;
  isFreelancer = false;
  /** True when user has Enterprise/Company owner role or owns at least one company. Used to show Create Company/Collaboration button. */
  isEnterprise = false;
  userLoadFailed = false;
  editingCollaboration: Collaboration | null = null;
  /** True when URL is /admin/collaborations — use this to always fetch all collaborations (no status param). */
  isAdminRoute = false;
  /** Toggle for Enterprise "What you can do" panel. */
  enterprisePanelOpen = false;
  /** Enterprise view: 'mine' = my company's collaborations, 'all' = all collaborations in the platform. */
  enterpriseViewMode: 'mine' | 'all' = 'mine';

  searchTerm = '';
  selectedStatus: CollaborationStatus | 'ALL' = 'ALL';
  selectedCompanyId: number | null = null;
  filterSkills = '';
  filterBudgetMin: number | null = null;
  filterBudgetMax: number | null = null;
  filterDuration = '';
  filterComplexity = '';
  filterIndustry = '';

  STATUS_OPTIONS: { value: CollaborationStatus | 'ALL'; label: string }[] = [
    { value: 'ALL', label: 'All statuses' },
    { value: 'OPEN', label: 'Open' },
    { value: 'MATCHED', label: 'Matched' },
    { value: 'IN_PROGRESS', label: 'In progress' },
    { value: 'COMPLETED', label: 'Completed' },
    { value: 'CANCELLED', label: 'Cancelled' },
    { value: 'ON_HOLD', label: 'On hold' },
    { value: 'ARCHIVED', label: 'Archived' }
  ];

  COLLAB_TYPES = ['ONE_TIME', 'RECURRING', 'LONG_TERM'];

  newCollaboration: CreateCollaborationRequest = {
    companyId: 0,
    title: '',
    description: '',
    collaborationType: 'ONE_TIME',
    requiredSkills: '',
    budgetMin: 0,
    budgetMax: 0,
    estimatedDuration: '',
    complexityLevel: '',
    deadline: '',
    confidentialityOption: false,
    industry: '',
    maxFreelancersNeeded: undefined,
    milestoneStructure: undefined
  };

  editCollaboration: UpdateCollaborationRequest = {};

  applyForm = {
    proposalMessage: '',
    proposedPrice: 0
  };

  private routerSub: any;

  constructor(
    private router: Router,
    private toast: ToastService,
    private collaborationService: CollaborationService,
    private companyService: CompanyService,
    private collaborationRequestService: CollaborationRequestService,
    private userService: UserService,
    private keycloakService: KeycloakService
  ) {}

  async ngOnInit() {
    this.updateAdminRoute();
    this.routerSub = this.router.events.pipe(
      filter((e): e is NavigationEnd => e instanceof NavigationEnd)
    ).subscribe(() => this.updateAdminRoute());
    await this.loadCurrentUser();
    await this.checkRoles();
    this.loadMyApplications();
    this.loadCompanies();
  }

  ngOnDestroy() {
    if (this.routerSub) this.routerSub.unsubscribe();
  }

  private updateAdminRoute() {
    this.isAdminRoute = this.router.url.includes('/admin/collaborations');
  }

  async loadCurrentUser() {
    try {
      const user = await this.userService.getCurrentUser().toPromise();
      if (user?.id) {
        this.currentUserId = user.id;
        this.currentUser = user;
        this.userLoadFailed = false;
      } else {
        this.userLoadFailed = true;
      }
    } catch (e) {
      this.userLoadFailed = true;
      console.error('Error loading current user', e);
    }
  }

  async checkRoles() {
    try {
      const roles = await this.keycloakService.getUserRoles();
      const roleList = roles || [];
      this.isAdmin = roleList.some((r: string) => String(r).toUpperCase() === 'ADMIN');
      this.isFreelancer = roleList.some((r: string) => {
        const u = String(r).toUpperCase();
        return u === 'FREELANCER' || u === 'FREELANCE';
      });
      this.isEnterprise = roleList.some((r: string) => {
        const u = String(r).toUpperCase();
        return u === 'ENTERPRISE' || u === 'COMPANY_OWNER' || u === 'EMPLOYER' || u.includes('ENTERPRISE') || u.includes('COMPANY');
      });
    } catch (e) {
      this.isAdmin = false;
      this.isFreelancer = false;
      this.isEnterprise = false;
    }
    // Fallback 1: backend user role (user-service). Keycloak may expose client roles or different names.
    if (this.currentUser?.role) {
      const r = String(this.currentUser.role).toUpperCase();
      if (r === 'FREELANCER' || r === 'FREELANCE') this.isFreelancer = true;
      if (r === 'ADMIN') this.isAdmin = true;
      if (r === 'ENTERPRISE' || r === 'COMPANY_OWNER' || r === 'EMPLOYER') this.isEnterprise = true;
    }
    // Fallback 2: roles stored at login (realm_access.roles from JWT)
    if (!this.isFreelancer || !this.isAdmin || !this.isEnterprise) {
      try {
        const stored = localStorage.getItem('kc-roles');
        if (stored) {
          const arr = JSON.parse(stored) as string[];
          if (Array.isArray(arr)) {
            if (!this.isFreelancer && arr.some((r: string) => /^freelance(r)?$/i.test(String(r).trim())))
              this.isFreelancer = true;
            if (!this.isAdmin && arr.some((r: string) => String(r).toUpperCase() === 'ADMIN'))
              this.isAdmin = true;
            if (!this.isEnterprise && arr.some((r: string) => /enterprise|company_owner|employer/i.test(String(r).trim())))
              this.isEnterprise = true;
          }
        }
      } catch (_) {}
    }
  }

  /** True if the user should see the Create Company / New Collaboration button. Only hide for freelancer with no companies. */
  get canCreateCompanyOrCollaboration(): boolean {
    return !(this.isFreelancer && this.companies.length === 0);
  }

  loadCompanies() {
    if (!this.currentUserId) {
      this.loadCollaborations();
      return;
    }
    this.companyService.getByOwnerId(this.currentUserId).subscribe({
      next: (list) => {
        this.companies = list;
        if (list.length && !this.newCollaboration.companyId) {
          this.newCollaboration.companyId = list[0].id!;
        }
        if (list.length && this.selectedCompanyId == null && !this.isAdmin) {
          this.selectedCompanyId = list[0].id!;
        }
        this.loadCollaborations();
      },
      error: (err) => {
        this.toast.error('Failed to load companies');
        this.loadCollaborations();
      }
    });
  }

  loadCollaborations() {
    this.isLoading = true;
    this.loadError = false;
    const isEnterprise = this.companies.length > 0;
    const companyId = this.isAdmin
      ? this.selectedCompanyId ?? null
      : (this.selectedCompanyId ?? this.companies[0]?.id ?? null);
    const status = this.selectedStatus === 'ALL' ? undefined : this.selectedStatus;

    // When on admin Collaborations page: always GET all (no status param). Backend was receiving status=OPEN otherwise.
    if (this.isAdminRoute) {
      this.collaborationService.getAllForAdmin().subscribe({
        next: (list) => { this.collaborations = list; this.applyFilters(); this.isLoading = false; },
        error: () => { this.isLoading = false; this.loadError = true; this.toast.error('Failed to load collaborations'); }
      });
      this.loadAllApplications();
      return;
    }
    if (this.isAdmin) {
      this.collaborationService.getAllForAdmin().subscribe({
        next: (list) => { this.collaborations = list; this.applyFilters(); this.isLoading = false; },
        error: () => { this.isLoading = false; this.loadError = true; this.toast.error('Failed to load collaborations'); }
      });
    } else if (isEnterprise && this.enterpriseViewMode === 'all') {
      this.collaborationService.getAll({ status }).subscribe({
        next: (list) => { this.collaborations = list; this.applyFilters(); this.isLoading = false; },
        error: () => { this.isLoading = false; this.loadError = true; this.toast.error('Failed to load collaborations'); }
      });
    } else if (isEnterprise && companyId) {
      this.collaborationService.getAll({ companyId, status }).subscribe({
        next: (list) => { this.collaborations = list; this.applyFilters(); this.isLoading = false; },
        error: () => { this.isLoading = false; this.loadError = true; this.toast.error('Failed to load collaborations'); }
      });
    } else {
      // Freelancer: fetch all collaborations when "All statuses" is selected so backend returns all 4; filter by status in UI.
      // When user selects a specific status (e.g. Open), send it to backend to reduce payload.
      const hasFilters = !!(this.filterSkills || this.filterBudgetMin != null || this.filterBudgetMax != null || this.filterDuration || this.filterComplexity || this.filterIndustry);
      const params: any = {};
      if (status) params.status = status;
      if (params.status === 'OPEN' && hasFilters) {
        params.skills = this.filterSkills || undefined;
        params.budgetMin = this.filterBudgetMin ?? undefined;
        params.budgetMax = this.filterBudgetMax ?? undefined;
        params.estimatedDuration = this.filterDuration || undefined;
        params.industry = this.filterIndustry || undefined;
      }
      this.collaborationService.getAll(params).subscribe({
        next: (list) => { this.collaborations = list; this.applyFilters(); this.isLoading = false; },
        error: () => { this.isLoading = false; this.loadError = true; this.toast.error('Failed to load collaborations'); }
      });
    }
  }

  loadMyApplications() {
    if (!this.currentUserId) return;
    this.collaborationRequestService.getByFreelancerId(this.currentUserId).subscribe({
      next: (list) => (this.myApplications = list),
      error: () => {}
    });
  }

  loadAllApplications() {
    if (!this.isAdminRoute) return;
    this.allApplicationsLoading = true;
    this.collaborationRequestService.getAll().subscribe({
      next: (list) => { this.allApplications = list; this.allApplicationsLoading = false; },
      error: () => { this.allApplicationsLoading = false; }
    });
  }

  applyFilters() {
    let list = [...this.collaborations];
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      list = list.filter(c => (c.title || '').toLowerCase().includes(term) || (c.requiredSkills || '').toLowerCase().includes(term));
    }
    if (this.selectedStatus && this.selectedStatus !== 'ALL') {
      list = list.filter(c => c.status === this.selectedStatus);
    }
    if (this.selectedCompanyId && this.isAdmin) {
      list = list.filter(c => c.companyId === this.selectedCompanyId);
    }
    if (this.filterComplexity && this.filterComplexity.trim()) {
      const comp = this.filterComplexity.trim().toLowerCase();
      list = list.filter(c => (c.complexityLevel || '').toLowerCase().includes(comp));
    }
    // Freelancers should not see MATCHED/IN_PROGRESS/COMPLETED collaborations unless they applied to them
    // OPEN collaborations are always visible (accepting applications)
    if (this.isFreelancer) {
      list = list.filter(c => {
        // Always show OPEN collaborations (accepting applications)
        if (c.status === 'OPEN') return true;
        // For non-OPEN collaborations (MATCHED, IN_PROGRESS, COMPLETED, etc.), only show if freelancer has applied
        return this.hasApplied(c.id!);
      });
    }
    this.filteredCollaborations = list;
  }

  onFilterChange() {
    this.loadCollaborations();
  }

  setEnterpriseViewMode(mode: 'mine' | 'all') {
    if (this.enterpriseViewMode === mode) return;
    this.enterpriseViewMode = mode;
    this.loadCollaborations();
  }

  openCreateCompany() {
    this.newCompanyName = '';
    this.showCreateCompanyModal = true;
  }

  createCompany() {
    if (!this.newCompanyName?.trim() || !this.currentUserId) {
      this.toast.error('Company name is required');
      return;
    }
    this.companyService.create({ name: this.newCompanyName.trim(), ownerId: this.currentUserId }).subscribe({
      next: () => {
        this.toast.success('Company created');
        this.showCreateCompanyModal = false;
        this.selectedStatus = 'ALL';
        this.searchTerm = '';
        this.loadCompanies();
      },
      error: (err) => this.toast.error(err.error?.error || err.error?.message || 'Create company failed')
    });
  }

  openCreate() {
    if (!this.companies.length) {
      this.openCreateCompany();
      return;
    }
    this.newCollaboration = {
      companyId: this.companies[0].id!,
      title: '',
      description: '',
      collaborationType: 'ONE_TIME',
      requiredSkills: '',
      budgetMin: 0,
      budgetMax: 0,
      estimatedDuration: '',
      complexityLevel: '',
      deadline: new Date().toISOString().slice(0, 16),
      confidentialityOption: false
    };
    this.showCreateModal = true;
  }

  createCollaboration() {
    if (!this.newCollaboration.title?.trim()) {
      this.toast.error('Title is required');
      return;
    }
    if (this.newCollaboration.budgetMax < this.newCollaboration.budgetMin) {
      this.toast.error('Max budget must be >= min budget');
      return;
    }
    const req = { ...this.newCollaboration, deadline: this.newCollaboration.deadline ? new Date(this.newCollaboration.deadline).toISOString() : new Date().toISOString() };
    this.collaborationService.create(req).subscribe({
      next: () => {
        this.toast.success('Collaboration created');
        this.showCreateModal = false;
        this.selectedStatus = 'ALL';
        this.searchTerm = '';
        this.loadCollaborations();
      },
      error: (err) => this.toast.error(err.error?.error || err.error?.message || 'Create failed')
    });
  }

  openEdit(c: Collaboration) {
    this.editingCollaboration = c;
    this.editCollaboration = {
      title: c.title,
      description: c.description,
      collaborationType: c.collaborationType,
      requiredSkills: c.requiredSkills,
      budgetMin: c.budgetMin,
      budgetMax: c.budgetMax,
      estimatedDuration: c.estimatedDuration,
      complexityLevel: c.complexityLevel,
      deadline: c.deadline ? c.deadline.slice(0, 16) : undefined,
      confidentialityOption: c.confidentialityOption,
      industry: c.industry,
      status: c.status
    };
    this.showEditModal = true;
  }

  saveEdit() {
    if (!this.editingCollaboration?.id) return;
    const companyId = this.editingCollaboration.companyId;
    const payload = { ...this.editCollaboration };
    if (payload.deadline) payload.deadline = new Date(payload.deadline).toISOString();
    const adminOverride = this.isAdmin;
    this.collaborationService.update(this.editingCollaboration.id, companyId, payload, adminOverride).subscribe({
      next: () => {
        this.toast.success('Collaboration updated');
        this.showEditModal = false;
        this.loadCollaborations();
        const editId = this.editingCollaboration?.id;
        if (editId != null && this.selectedCollaboration?.id === editId) {
          this.collaborationService.getById(editId).subscribe({
            next: (c) => (this.selectedCollaboration = c)
          });
        }
      },
      error: (err) => this.toast.error(err.error?.error || 'Update failed')
    });
  }

  openDetail(c: Collaboration) {
    this.selectedCollaboration = c;
    this.showDetailModal = true;
    this.loadApplicationsForDetail();
  }

  openDetailById(collaborationId: number) {
    this.collaborationService.getById(collaborationId).subscribe({
      next: (c) => this.openDetail(c),
      error: () => this.toast.error('Collaboration not found')
    });
  }

  private loadApplicationsForDetail() {
    const c = this.selectedCollaboration;
    if (!c) return;
    this.applications = [];
    if (this.canManageCollaboration(c) || this.isAdminRoute) {
      this.collaborationRequestService.getByCollaborationId(c.id!).subscribe({
        next: (list) => (this.applications = list),
        error: () => (this.applications = [])
      });
    }
  }

  closeDetail() {
    this.showDetailModal = false;
    this.selectedCollaboration = null;
    this.applications = [];
  }

  deleteCollaboration(c: Collaboration) {
    const adminOverride = this.isAdmin || this.isAdminRoute;
    const msg = adminOverride ? 'Force delete this collaboration? (Admin)' : 'Delete this collaboration?';
    if (!confirm(msg)) return;
    this.collaborationService.delete(c.id!, c.companyId, adminOverride).subscribe({
      next: () => {
        this.toast.success('Collaboration deleted');
        this.closeDetail();
        this.loadCollaborations();
        if (this.isAdminRoute) this.loadAllApplications();
      },
      error: (err) => this.toast.error(err.error?.error || 'Delete failed')
    });
  }

  archiveCollaboration(c: Collaboration) {
    if (!confirm('Archive this collaboration? It will be hidden from normal listings.')) return;
    this.collaborationService.updateStatus(c.id!, 'ARCHIVED', c.companyId, true).subscribe({
      next: () => {
        this.toast.success('Collaboration archived');
        if (this.selectedCollaboration?.id === c.id) {
          this.selectedCollaboration = { ...this.selectedCollaboration, status: 'ARCHIVED' } as Collaboration;
        }
        this.loadCollaborations();
      },
      error: (err) => this.toast.error(err.error?.error || 'Archive failed')
    });
  }

  /** Admin: force status change (cancel, complete, freeze) */
  setCollaborationStatus(c: Collaboration, status: CollaborationStatus) {
    const labels: Record<string, string> = {
      CANCELLED: 'Cancel this collaboration?',
      COMPLETED: 'Mark this collaboration as completed?',
      ON_HOLD: 'Freeze this project? (Set on hold)'
    };
    if (!confirm(labels[status] || `Set status to ${status}?`)) return;
    this.collaborationService.updateStatus(c.id!, status, c.companyId, true).subscribe({
      next: () => {
        this.toast.success('Status updated');
        if (this.selectedCollaboration?.id === c.id) {
          this.selectedCollaboration = { ...this.selectedCollaboration, status } as Collaboration;
        }
        this.loadCollaborations();
        if (this.isAdminRoute) this.loadAllApplications();
      },
      error: (err) => this.toast.error(err.error?.error || 'Status update failed')
    });
  }

  openApplyModal(c: Collaboration) {
    this.selectedCollaboration = c;
    this.applyForm = { proposalMessage: '', proposedPrice: c.budgetMin || 0 };
    this.showApplyModal = true;
  }

  submitApplication() {
    if (!this.selectedCollaboration?.id || !this.currentUserId) return;
    this.collaborationRequestService.create({
      collaborationId: this.selectedCollaboration.id,
      freelancerId: this.currentUserId,
      proposalMessage: this.applyForm.proposalMessage,
      proposedPrice: this.applyForm.proposedPrice
    }).subscribe({
      next: () => {
        this.toast.success('Application submitted');
        this.showApplyModal = false;
        this.loadMyApplications();
        this.loadCollaborations();
      },
      error: (err) => this.toast.error(err.error?.error || 'Apply failed')
    });
  }

  acceptApplication(app: CollaborationRequestDto) {
    if (!this.selectedCollaboration?.companyId) return;
    this.collaborationRequestService.updateStatus(app.id!, this.selectedCollaboration.companyId, 'ACCEPTED').subscribe({
      next: () => {
        this.toast.success('Application accepted');
        this.loadCollaborations();
        this.collaborationRequestService.getByCollaborationId(this.selectedCollaboration!.id!).subscribe({
          next: (list) => (this.applications = list)
        });
      },
      error: (err) => this.toast.error(err.error?.error || 'Accept failed')
    });
  }

  rejectApplication(app: CollaborationRequestDto) {
    if (!this.selectedCollaboration?.companyId) return;
    this.collaborationRequestService.updateStatus(app.id!, this.selectedCollaboration.companyId, 'REJECTED').subscribe({
      next: () => {
        this.toast.success('Application rejected');
        this.collaborationRequestService.getByCollaborationId(this.selectedCollaboration!.id!).subscribe({
          next: (list) => (this.applications = list)
        });
      },
      error: (err) => this.toast.error(err.error?.error || 'Reject failed')
    });
  }

  withdrawApplication(app: CollaborationRequestDto) {
    if (!this.currentUserId) return;
    this.collaborationRequestService.withdraw(app.id!, this.currentUserId).subscribe({
      next: () => {
        this.toast.success('Application withdrawn');
        this.loadMyApplications();
        this.loadCollaborations();
      },
      error: (err) => this.toast.error(err.error?.error || 'Withdraw failed')
    });
  }

  canManageCollaboration(c: Collaboration): boolean {
    if (this.isAdmin) return true;
    const company = this.companies.find(co => co.id === c.companyId);
    return !!company;
  }

  hasApplied(collaborationId: number): boolean {
    return this.myApplications.some(a => a.collaborationId === collaborationId);
  }

  getApplicationStatus(collaborationId: number): string {
    const app = this.myApplications.find(a => a.collaborationId === collaborationId);
    return app?.status || '';
  }

  formatCurrency(n: number): string {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(n);
  }

  formatDate(s: string | undefined): string {
    if (!s) return '-';
    return new Date(s).toLocaleDateString();
  }

  getCompanyName(companyId: number): string {
    return this.companies.find(c => c.id === companyId)?.name || `Company #${companyId}`;
  }

  /** Collaboration title for display (e.g. in My applications). Resolves from loaded collaborations when available. */
  getCollaborationTitle(collabId: number): string {
    const c = this.collaborations.find(x => x.id === collabId);
    return c?.title || `Collaboration #${collabId}`;
  }

  /** Role context label for the current user (for UI badge). */
  get roleContextLabel(): string {
    if (this.isAdminRoute) return 'Moderation (Admin)';
    if (this.isFreelancer) return 'Freelancer';
    if (this.companies.length) return 'Enterprise';
    return 'Projects';
  }

  /** Enterprise: counts by status when viewing "My collaborations". */
  get enterpriseMyStats(): { open: number; matched: number; in_progress: number; completed: number; other: number } | null {
    if (this.isFreelancer || this.enterpriseViewMode !== 'mine') return null;
    const list = this.collaborations;
    return {
      open: list.filter(c => c.status === 'OPEN').length,
      matched: list.filter(c => c.status === 'MATCHED').length,
      in_progress: list.filter(c => c.status === 'IN_PROGRESS').length,
      completed: list.filter(c => c.status === 'COMPLETED').length,
      other: list.filter(c => !['OPEN', 'MATCHED', 'IN_PROGRESS', 'COMPLETED'].includes(c.status || '')).length
    };
  }

  /** Admin: pending applications count. */
  get adminPendingCount(): number {
    return this.allApplications.filter(a => a.status === 'PENDING').length;
  }

  /** Resolve company ID for an application (from loaded collaborations). Used for accept/reject from list. */
  getCompanyIdForApplication(app: CollaborationRequestDto): number | null {
    const c = this.collaborations.find(x => x.id === app.collaborationId);
    return c?.companyId ?? null;
  }

  acceptApplicationFromList(app: CollaborationRequestDto) {
    const companyId = this.selectedCollaboration?.companyId ?? this.getCompanyIdForApplication(app);
    if (!app.id || companyId == null) {
      this.toast.error('Cannot resolve company for this application');
      return;
    }
    this.collaborationRequestService.updateStatus(app.id, companyId, 'ACCEPTED').subscribe({
      next: () => {
        this.toast.success('Application accepted');
        this.loadAllApplications();
        this.loadCollaborations();
      },
      error: (err) => this.toast.error(err.error?.error || 'Accept failed')
    });
  }

  rejectApplicationFromList(app: CollaborationRequestDto) {
    const companyId = this.selectedCollaboration?.companyId ?? this.getCompanyIdForApplication(app);
    if (!app.id || companyId == null) {
      this.toast.error('Cannot resolve company for this application');
      return;
    }
    this.collaborationRequestService.updateStatus(app.id, companyId, 'REJECTED').subscribe({
      next: () => {
        this.toast.success('Application rejected');
        this.loadAllApplications();
        this.loadCollaborations();
      },
      error: (err) => this.toast.error(err.error?.error || 'Reject failed')
    });
  }

  /**
   * Navigate to the workspace/project management view for a collaboration
   */
  openWorkspace(collaborationId: number): void {
    this.router.navigate(['/workspace', collaborationId]);
  }

  /**
   * Open negotiation chat modal for an application
   */
  openNegotiation(app: CollaborationRequestDto): void {
    this.selectedApplicationForNegotiation = app;
    this.showNegotiationModal = true;
  }

  /**
   * Open counter-offer modal for an application
   */
  openCounterOffer(app: CollaborationRequestDto): void {
    this.selectedApplicationForNegotiation = app;
    this.showCounterOfferModal = true;
  }
  
  /**
   * Open counter-offer modal from negotiation chat
   */
  openCounterOfferFromChat(): void {
    // Close negotiation modal and open counter-offer modal
    this.showNegotiationModal = false;
    this.showCounterOfferModal = true;
  }

  /**
   * Handle counter-offer sent event
   */
  onCounterOfferSent(response: any): void {
    this.showCounterOfferModal = false;
    this.toast.success('Counter-offer sent successfully!');
    // Reload applications to show updated data
    this.loadApplicationsForDetail();
    if (this.isAdminRoute) {
      this.loadAllApplications();
    }
    if (this.isFreelancer) {
      this.loadMyApplications();
    }
  }

  /**
   * Check if user can send counter-offer for an application
   */
  canSendCounterOffer(app: CollaborationRequestDto): boolean {
    if (!app || app.status !== 'PENDING') return false;
    // Freelancer can counter-offer if company has made an offer
    if (this.isFreelancer && app.counterOfferedBy && app.counterOfferedBy !== this.currentUserId) {
      return true;
    }
    // Company can counter-offer if freelancer has applied or countered
    if (!this.isFreelancer && this.canManageCollaboration(this.selectedCollaboration!)) {
      return true;
    }
    return false;
  }

  /**
   * Get negotiation status badge class
   */
  getNegotiationStatusClass(status?: string): string {
    if (!status) return 'negotiation-initial';
    return `negotiation-${status.toLowerCase().replace(/_/g, '-')}`;
  }

  /**
   * Get negotiation status label
   */
  getNegotiationStatusLabel(status?: string): string {
    if (!status || status === 'INITIAL') return 'New';
    if (status === 'NEGOTIATING') return 'In Discussion';
    if (status === 'COUNTER_OFFERED') return 'Counter-Offered';
    if (status === 'AGREED') return 'Terms Agreed';
    if (status === 'DECLINED') return 'Declined';
    return status;
  }
}

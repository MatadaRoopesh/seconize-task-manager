import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin, finalize } from 'rxjs';

interface Task {
  id: number;
  title: string;
  description?: string;
  assignee?: string;
  status: string;
  priority: string;
  deadline?: string;
  projectId?: number;
  parentTaskId?: number | null;
  parentTask?: { id: number } | null;
  subtasks?: Task[];
}

interface TaskComment {
  id: number;
  taskId: number;
  content: string;
  author: string;
  createdAt: string;
}


interface TaskActivity {
  id: number;
  taskId: number;
  action: string;
  oldStatus: string | null;
  newStatus: string | null;
  createdAt: string;
}


interface Project {
  id: number;
  name: string;
  description?: string;
  status: string;
  startDate?: string;
  deadline?: string;
}

interface CreateProjectRequest {
  name: string;
  description: string;
  status: string;
  startDate: string;
  deadline: string;
}

interface EditProjectForm {
  name: string;
  description: string;
  status: string;
  startDate: string;
  deadline: string;
}

interface AiTaskSuggestion {
  title: string;
  description: string;
  priority: string;
}

interface AiTaskBreakdownRequest {
  projectDescription: string;
}

interface CreateTaskRequest {
  title: string;
  description: string;
  assignee: string;
  deadline: string;
  status: string;
  priority: string;
}

interface NewTaskForm extends CreateTaskRequest {
  projectId: number | null;
}

interface SubtaskForm {
  title: string;
  description: string;
  assignee: string;
  deadline: string;
  status: string;
  priority: string;
}

interface EditTaskForm {
  title: string;
  description: string;
  assignee: string;
  deadline: string;
  status: string;
  priority: string;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly cdr = inject(ChangeDetectorRef);

  private readonly apiUrl =
    'https://seconize-task-manager.onrender.com/api';

  tasks: Task[] = [];
  projects: Project[] = [];

  // Create project form
  showProjectForm = false;
  savingProject = false;
  projectMessage = '';
  projectError = '';

  newProject: CreateProjectRequest = this.emptyProject();

  // Edit project form
  editingProjectId: number | null = null;
  savingProjectEdit = false;

  editProject: EditProjectForm = this.emptyEditProject();

  private emptyEditProject(): EditProjectForm {
    return {
      name: '',
      description: '',
      status: 'PLANNING',
      startDate: '',
      deadline: ''
    };
  }

  private emptyProject(): CreateProjectRequest {
    return {
      name: '',
      description: '',
      status: 'PLANNING',
      startDate: '',
      deadline: ''
    };
  }

  // AI task generator
  aiProjectDescription = '';
  aiSuggestions: AiTaskSuggestion[] = [];
  aiLoading = false;
  aiError = '';
  aiMessage = '';
  savingAiTasks = false;

  loading = false;
  error = '';
  search = '';
  selectedStatus = 'ALL';

  showCreateForm = false;
  savingTask = false;
  taskMessage = '';
  taskError = '';

  editingTaskId: number | null = null;
  deletingTaskId: number | null = null;

  selectedCommentTask: Task | null = null;
  comments: TaskComment[] = [];

  selectedActivityTask: Task | null = null;
  activities: TaskActivity[] = [];
  activityLoading = false;
  activityError = '';

  commentDraft = '';
  commentLoading = false;
  commentSaving = false;
  commentError = '';
  commentMessage = '';

  expandedSubtaskParentId: number | null = null;
  subtasksByParent: Record<number, Task[]> = {};
  subtaskForms: Record<number, SubtaskForm> = {};
  subtaskLoadingParentId: number | null = null;
  subtaskSavingParentId: number | null = null;
  subtaskError = '';
  subtaskMessage = '';

  editTask: EditTaskForm = this.emptyEditTask();
  newTask: NewTaskForm = this.emptyTask();

  private emptyTask(): NewTaskForm {
    return {
      projectId: null,
      title: '',
      description: '',
      assignee: '',
      deadline: '',
      status: 'TODO',
      priority: 'MEDIUM'
    };
  }

  private emptyEditTask(): EditTaskForm {
    return {
      title: '',
      description: '',
      assignee: '',
      deadline: '',
      status: 'TODO',
      priority: 'MEDIUM'
    };
  }

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading = true;
    this.error = '';
    this.cdr.detectChanges();

    console.log('Loading dashboard from:', this.apiUrl);

    forkJoin({
      tasks: this.http.get<Task[]>(`${this.apiUrl}/tasks`),
      projects: this.http.get<Project[]>(`${this.apiUrl}/projects`)
    })
      .pipe(
        finalize(() => {
          this.loading = false;
          this.cdr.detectChanges();
          console.log('Dashboard loading finished.');
        })
      )
      .subscribe({
        next: ({ tasks, projects }) => {
          this.tasks = tasks ?? [];
          this.projects = projects ?? [];

          console.log('Tasks assigned:', this.tasks.length);
          console.log('Projects assigned:', this.projects.length);

          if (
            this.newTask.projectId === null &&
            this.projects.length > 0
          ) {
            this.newTask.projectId = this.projects[0].id;
          }

          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Dashboard API error:', err);

          this.error =
            err.error?.message ??
            err.error?.detail ??
            'Could not load dashboard data. Check the backend and API connection.';

          this.cdr.detectChanges();
        }
      });
  }

  // Create project
  toggleProjectForm(): void {
    if (this.savingProject) {
      return;
    }

    this.projectError = '';
    this.projectMessage = '';
    this.showProjectForm = !this.showProjectForm;
  }

  createProject(): void {
    this.projectError = '';
    this.projectMessage = '';

    if (this.savingProject) {
      return;
    }

    if (!this.newProject.name.trim()) {
      this.projectError = 'Please enter a project name.';
      return;
    }

    if (!this.newProject.startDate || !this.newProject.deadline) {
      this.projectError = 'Please select both project dates.';
      return;
    }

    if (this.newProject.deadline < this.newProject.startDate) {
      this.projectError = 'Deadline cannot be before the start date.';
      return;
    }

    const request: CreateProjectRequest = {
      name: this.newProject.name.trim(),
      description: this.newProject.description.trim(),
      status: this.newProject.status,
      startDate: this.newProject.startDate,
      deadline: this.newProject.deadline
    };

    this.savingProject = true;

    this.http
      .post<Project>(`${this.apiUrl}/projects`, request)
      .pipe(
        finalize(() => {
          this.savingProject = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (project) => {
          this.projectMessage =
            `Project "${project.name}" created successfully.`;
          this.projectError = '';
          this.newProject = this.emptyProject();
          this.showProjectForm = false;
          this.loadDashboard();
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Create project API error:', err);
          this.projectError =
            err.error?.message ??
            err.error?.detail ??
            'Could not create the project. Check the backend API.';
          this.cdr.detectChanges();
        }
      });

  }

  // Open the edit project form
  startEditProject(project: Project): void {
    this.projectError = '';
    this.projectMessage = '';

    this.showProjectForm = false;
    this.editingProjectId = project.id;

    this.editProject = {
      name: project.name ?? '',
      description: project.description ?? '',
      status: project.status ?? 'PLANNING',
      startDate: project.startDate ?? '',
      deadline: project.deadline ?? ''
    };
  }

  // Cancel project editing
  cancelEditProject(): void {
    if (this.savingProjectEdit) {
      return;
    }

    this.editingProjectId = null;
    this.editProject = this.emptyEditProject();
    this.projectError = '';
  }

  // Save project changes
  updateProject(): void {
    this.projectError = '';
    this.projectMessage = '';

    if (this.editingProjectId === null || this.savingProjectEdit) {
      return;
    }

    if (!this.editProject.name.trim()) {
      this.projectError = 'Please enter a project name.';
      return;
    }

    if (!this.editProject.startDate || !this.editProject.deadline) {
      this.projectError = 'Please select both project dates.';
      return;
    }

    if (this.editProject.deadline < this.editProject.startDate) {
      this.projectError = 'Deadline cannot be before the start date.';
      return;
    }

    const projectId = this.editingProjectId;

    const request: CreateProjectRequest = {
      name: this.editProject.name.trim(),
      description: this.editProject.description.trim(),
      status: this.editProject.status,
      startDate: this.editProject.startDate,
      deadline: this.editProject.deadline
    };

    this.savingProjectEdit = true;

    this.http
      .put<Project>(`${this.apiUrl}/projects/${projectId}`, request)
      .pipe(
        finalize(() => {
          this.savingProjectEdit = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (project) => {
          this.projectMessage =
            `Project "${project.name}" updated successfully.`;
          this.editingProjectId = null;
          this.editProject = this.emptyEditProject();
          this.loadDashboard();
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Update project API error:', err);
          this.projectError =
            err.error?.message ??
            err.error?.detail ??
            'Could not update the project.';
          this.cdr.detectChanges();
        }
      });
  }

  // Delete project after confirmation
  deleteProject(project: Project): void {
    this.projectError = '';
    this.projectMessage = '';

    if (
      this.savingProject ||
      this.savingProjectEdit ||
      !confirm(`Are you sure you want to delete "${project.name}"?`)
    ) {
      return;
    }

    this.http
      .delete(`${this.apiUrl}/projects/${project.id}`)
      .pipe(
        finalize(() => {
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: () => {
          this.projectMessage =
            `Project "${project.name}" deleted successfully.`;

          if (this.editingProjectId === project.id) {
            this.editingProjectId = null;
            this.editProject = this.emptyEditProject();
          }

          this.loadDashboard();
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Delete project API error:', err);
          this.projectError =
            err.error?.message ??
            err.error?.detail ??
            'Could not delete the project. It may still contain tasks.';
          this.cdr.detectChanges();
        }
      });
  }


  // Create task
  toggleCreateForm(): void {
    this.taskError = '';
    this.taskMessage = '';
    this.showCreateForm = !this.showCreateForm;

    if (
      this.showCreateForm &&
      this.newTask.projectId === null &&
      this.projects.length > 0
    ) {
      this.newTask.projectId = this.projects[0].id;
    }
  }

  closeCreateForm(): void {
    if (this.savingTask) {
      return;
    }

    this.showCreateForm = false;
    this.taskError = '';
  }

  createTask(): void {
    this.taskMessage = '';
    this.taskError = '';

    if (!this.newTask.title.trim()) {
      this.taskError = 'Please enter a task title.';
      return;
    }

    if (this.newTask.projectId === null) {
      this.taskError = 'Please select a project.';
      return;
    }

    if (!this.newTask.deadline) {
      this.taskError = 'Please select a deadline.';
      return;
    }

    const projectId = this.newTask.projectId;

    const request: CreateTaskRequest = {
      title: this.newTask.title.trim(),
      description: this.newTask.description.trim(),
      assignee: this.newTask.assignee.trim(),
      deadline: this.newTask.deadline,
      status: this.newTask.status,
      priority: this.newTask.priority
    };

    this.savingTask = true;

    this.http
      .post<Task>(
        `${this.apiUrl}/projects/${projectId}/tasks`,
        request
      )
      .pipe(
        finalize(() => {
          this.savingTask = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (createdTask) => {
          this.taskMessage =
            `Task "${createdTask.title}" created successfully.`;

          this.taskError = '';
          this.newTask = this.emptyTask();
          this.newTask.projectId = projectId;
          this.showCreateForm = false;

          this.cdr.detectChanges();
          this.loadDashboard();
        },
        error: (err) => {
          console.error('Create task API error:', err);

          this.taskError =
            err.error?.message ??
            err.error?.detail ??
            'Could not create the task. Check the backend logs and API response.';

          this.cdr.detectChanges();
        }
      });
  }

  startEdit(task: Task): void {
    this.taskMessage = '';
    this.taskError = '';
    this.editingTaskId = task.id;

    this.editTask = {
      title: task.title ?? '',
      description: task.description ?? '',
      assignee: task.assignee ?? '',
      deadline: task.deadline ?? '',
      status: task.status ?? 'TODO',
      priority: task.priority ?? 'MEDIUM'
    };

    this.cdr.detectChanges();
  }

  cancelEdit(): void {
    if (this.savingTask) {
      return;
    }

    this.editingTaskId = null;
    this.editTask = this.emptyEditTask();
    this.taskError = '';
    this.cdr.detectChanges();
  }

  saveEdit(): void {
    if (this.editingTaskId === null || this.savingTask) {
      return;
    }

    this.taskMessage = '';
    this.taskError = '';

    if (!this.editTask.title.trim()) {
      this.taskError = 'Task title cannot be empty.';
      return;
    }

    const taskId = this.editingTaskId;

    const request: CreateTaskRequest = {
      title: this.editTask.title.trim(),
      description: this.editTask.description.trim(),
      assignee: this.editTask.assignee.trim(),
      deadline: this.editTask.deadline,
      status: this.editTask.status,
      priority: this.editTask.priority
    };

    this.savingTask = true;

    this.http
      .put<Task>(`${this.apiUrl}/tasks/${taskId}`, request)
      .pipe(
        finalize(() => {
          this.savingTask = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (updatedTask) => {
          this.taskMessage =
            `Task "${updatedTask.title}" updated successfully.`;

          this.taskError = '';
          this.editingTaskId = null;
          this.editTask = this.emptyEditTask();

          this.cdr.detectChanges();
          this.loadDashboard();
        },
        error: (err) => {
          console.error('Update task API error:', err);

          this.taskError =
            err.error?.message ??
            err.error?.detail ??
            'Could not update the task. Please check the backend logs.';

          this.cdr.detectChanges();
        }
      });
  }

  deleteTask(task: Task): void {
    if (this.deletingTaskId !== null || this.savingTask) {
      return;
    }

    const confirmed = window.confirm(
      `Are you sure you want to delete "${task.title}"? This action cannot be undone.`
    );

    if (!confirmed) {
      return;
    }

    this.taskMessage = '';
    this.taskError = '';
    this.deletingTaskId = task.id;

    this.http
      .delete<void>(`${this.apiUrl}/tasks/${task.id}`)
      .pipe(
        finalize(() => {
          this.deletingTaskId = null;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: () => {
          this.taskMessage = `Task "${task.title}" deleted successfully.`;

          if (this.editingTaskId === task.id) {
            this.cancelEdit();
          }

          this.cdr.detectChanges();
          this.loadDashboard();
        },
        error: (err) => {
          console.error('Delete task API error:', err);

          this.taskError =
            err.error?.message ??
            err.error?.detail ??
            'Could not delete the task. It may have subtasks that must be deleted first.';

          this.cdr.detectChanges();
        }
      });
  }

  isSubtask(task: Task): boolean {
    return task.parentTaskId != null || task.parentTask?.id != null;
  }

  parentIdOf(task: Task): number | null {
    return task.parentTaskId ?? task.parentTask?.id ?? null;
  }

  toggleSubtasks(task: Task): void {
    this.subtaskError = '';
    this.subtaskMessage = '';

    if (this.expandedSubtaskParentId === task.id) {
      this.expandedSubtaskParentId = null;
      return;
    }

    this.expandedSubtaskParentId = task.id;

    if (!this.subtaskForms[task.id]) {
      this.subtaskForms[task.id] = this.emptySubtaskForm();
    }

    this.loadSubtasks(task);
    this.cdr.detectChanges();

    window.setTimeout(() => {
      document.getElementById(`subtasks-${task.id}`)?.scrollIntoView({
        behavior: 'smooth',
        block: 'nearest'
      });
    }, 100);
  }

  private emptySubtaskForm(): SubtaskForm {
    return {
      title: '',
      description: '',
      assignee: '',
      deadline: '',
      status: 'TODO',
      priority: 'MEDIUM'
    };
  }

  loadSubtasks(task: Task): void {
    this.subtaskLoadingParentId = task.id;

    this.http
      .get<Task[]>(`${this.apiUrl}/tasks/${task.id}/subtasks`)
      .pipe(
        finalize(() => {
          if (this.subtaskLoadingParentId === task.id) {
            this.subtaskLoadingParentId = null;
          }
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (items) => {
          this.subtasksByParent[task.id] = items ?? [];
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Load subtasks API error:', err);

          this.subtaskError =
            err.error?.message ??
            err.error?.detail ??
            `Could not load subtasks for "${task.title}".`;

          this.cdr.detectChanges();
        }
      });
  }

  createSubtask(parent: Task): void {
    const form =
      this.subtaskForms[parent.id] ?? this.emptySubtaskForm();

    this.subtaskError = '';
    this.subtaskMessage = '';

    if (!form.title.trim()) {
      this.subtaskError = 'Enter a title for the subtask.';
      return;
    }

    if (!form.deadline) {
      this.subtaskError = 'Select a deadline for the subtask.';
      return;
    }

    this.subtaskSavingParentId = parent.id;

    const request = {
      title: form.title.trim(),
      description: form.description.trim(),
      assignee: form.assignee.trim(),
      deadline: form.deadline,
      status: form.status,
      priority: form.priority
    };

    this.http
      .post<Task>(
        `${this.apiUrl}/tasks/${parent.id}/subtasks`,
        request
      )
      .pipe(
        finalize(() => {
          this.subtaskSavingParentId = null;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (created) => {
          this.subtaskForms[parent.id] = this.emptySubtaskForm();
          this.subtaskMessage =
            `Subtask "${created.title}" created successfully.`;

          this.loadSubtasks(parent);
          this.loadDashboard();
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Create subtask API error:', err);

          this.subtaskError =
            err.error?.message ??
            err.error?.detail ??
            'Could not create the subtask. Check the backend API and try again.';

          this.cdr.detectChanges();
        }
      });
  }

  updateSubtaskStatus(
    subtask: Task,
    status: string,
    parent: Task
  ): void {
    const request = {
      title: subtask.title,
      description: subtask.description ?? '',
      assignee: subtask.assignee ?? '',
      deadline: subtask.deadline ?? '',
      status,
      priority: subtask.priority
    };

    this.http
      .put<Task>(`${this.apiUrl}/tasks/${subtask.id}`, request)
      .subscribe({
        next: () => {
          this.subtaskMessage = `Subtask "${subtask.title}" updated.`;
          this.loadSubtasks(parent);
          this.loadDashboard();
        },
        error: (err) => {
          this.subtaskError =
            err.error?.message ??
            err.error?.detail ??
            'Could not update the subtask status.';

          this.cdr.detectChanges();
        }
      });
  }

  // Open a task's activity history
  openActivity(task: Task): void {
    this.selectedActivityTask = task;
    this.activities = [];
    this.activityError = '';
    this.activityLoading = true;
    this.cdr.detectChanges();

    window.setTimeout(() => {
      document.getElementById('task-activity')?.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
      });
    }, 100);

    this.http
      .get<TaskActivity[]>(`${this.apiUrl}/tasks/${task.id}/activity`)
      .pipe(
        finalize(() => {
          this.activityLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (activities) => {
          this.activities = activities ?? [];
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Load activity history API error:', err);
          this.activityError =
            err.error?.message ??
            err.error?.detail ??
            'Could not load activity history. Check that the backend activity API is deployed.';
          this.cdr.detectChanges();
        }
      });
  }

  // Close the activity history panel
  closeActivity(): void {
    this.selectedActivityTask = null;
    this.activities = [];
    this.activityError = '';
  }

  // Format activity timestamps for display
  formatActivityDate(value: string): string {
    if (!value) {
      return '';
    }

    const date = new Date(value);
    return Number.isNaN(date.getTime())
      ? value
      : date.toLocaleString();
  }

  openComments(task: Task): void {
    this.selectedCommentTask = task;
    this.comments = [];
    this.commentDraft = '';
    this.commentError = '';
    this.commentMessage = '';
    this.commentLoading = true;

    this.cdr.detectChanges();

    window.setTimeout(() => {
      document.getElementById('task-comments')?.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
      });
    }, 100);

    this.http
      .get<TaskComment[]>(`${this.apiUrl}/tasks/${task.id}/comments`)
      .pipe(
        finalize(() => {
          this.commentLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (comments) => {
          this.comments = comments ?? [];
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Load comments API error:', err);

          this.commentError =
            err.error?.message ??
            err.error?.detail ??
            'Could not load comments. Check the backend and try again.';

          this.cdr.detectChanges();
        }
      });
  }

  closeComments(): void {
    if (this.commentSaving) {
      return;
    }

    this.selectedCommentTask = null;
    this.comments = [];
    this.commentDraft = '';
    this.commentError = '';
    this.commentMessage = '';
  }

  addComment(): void {
    const task = this.selectedCommentTask;
    const content = this.commentDraft.trim();

    if (!task || this.commentSaving) {
      return;
    }

    this.commentError = '';
    this.commentMessage = '';

    if (!content) {
      this.commentError = 'Please enter a comment before submitting.';
      return;
    }

    if (content.length > 2000) {
      this.commentError = 'Comments cannot exceed 2000 characters.';
      return;
    }

    this.commentSaving = true;

    this.http
      .post<TaskComment>(`${this.apiUrl}/tasks/${task.id}/comments`, {
        content,
        author: 'Roopesh'
      })
      .pipe(
        finalize(() => {
          this.commentSaving = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (createdComment) => {
          this.comments = [...this.comments, createdComment];
          this.commentDraft = '';
          this.commentMessage = 'Comment added successfully.';
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Create comment API error:', err);

          this.commentError =
            err.error?.message ??
            err.error?.detail ??
            'Could not add the comment. Please try again.';

          this.cdr.detectChanges();
        }
      });
  }

  formatCommentDate(value: string): string {
    if (!value) {
      return '';
    }

    const date = new Date(value);
    return Number.isNaN(date.getTime())
      ? value
      : date.toLocaleString();
  }

  // Kanban board columns
  kanbanColumns = [
    { status: 'TODO', label: 'To Do' },
    { status: 'IN_PROGRESS', label: 'In Progress' },
    { status: 'DONE', label: 'Done' }
  ];

  tasksForStatus(status: string): Task[] {
    return this.tasks.filter(task => task.status === status);
  }

  moveTask(task: Task, newStatus: string): void {
    if (task.status === newStatus) {
      return;
    }

    const previousStatus = task.status;
    this.taskError = '';
    this.taskMessage = '';

    const updatedTask: Task = {
      ...task,
      status: newStatus
    };

    this.http
      .put<Task>(`${this.apiUrl}/tasks/${task.id}`, updatedTask)
      .subscribe({
        next: (response) => {
          task.status = response?.status ?? newStatus;

          this.taskMessage =
            `Task "${task.title}" moved successfully.`;
          this.taskError = '';

          this.loadDashboard();
          this.cdr.detectChanges();
        },
        error: (err) => {
          task.status = previousStatus;

          console.error('Kanban status update failed:', err);

          const messages = err.error?.messages;
          this.taskError =
            messages?.title ??
            err.error?.message ??
            err.error?.detail ??
            'Could not move the task. Please try again.';

          this.cdr.detectChanges();
        }
      });
  }

  statusLabel(status: string): string {
    switch (status) {
      case 'TODO':
        return 'To Do';
      case 'IN_PROGRESS':
        return 'In Progress';
      case 'DONE':
        return 'Done';
      default:
        return status?.replace(/_/g, ' ') ?? 'Unknown';
    }
  }

  get filteredTasks(): Task[] {
    const searchText = this.search.toLowerCase().trim();

    return this.tasks.filter((task) => {
      const matchesSearch =
        (task.title ?? '').toLowerCase().includes(searchText) ||
        (task.assignee ?? '').toLowerCase().includes(searchText) ||
        (task.description ?? '').toLowerCase().includes(searchText);

      const matchesStatus =
        this.selectedStatus === 'ALL' ||
        task.status === this.selectedStatus;

      return matchesSearch && matchesStatus;
    });
  }

  generateAiTasks(): void {
    const projectDescription = this.aiProjectDescription.trim();
    this.aiError = '';
    this.aiMessage = '';
    this.aiSuggestions = [];

    if (!projectDescription) {
      this.aiError = 'Please enter a project description.';
      return;
    }

    this.aiLoading = true;
    const request: AiTaskBreakdownRequest = { projectDescription };

    this.http
      .post<AiTaskSuggestion[]>(`${this.apiUrl}/ai/task-breakdown`, request)
      .pipe(
        finalize(() => {
          this.aiLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (suggestions) => {
          this.aiSuggestions = Array.isArray(suggestions) ? suggestions : [];
          if (this.aiSuggestions.length === 0) {
            this.aiMessage = 'No task suggestions were generated.';
          }
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('AI task generation error:', err);
          this.aiError =
            err.error?.message ??
            err.error?.detail ??
            'Could not generate tasks. Check that the backend and Gemini API are working.';
          this.cdr.detectChanges();
        }
      });
  }

  removeAiSuggestion(index: number): void {
    this.aiSuggestions = this.aiSuggestions.filter((_, i) => i !== index);
  }

  clearAiSuggestions(): void {
    this.aiSuggestions = [];
    this.aiError = '';
    this.aiMessage = '';
  }

  saveAiSuggestions(): void {
    this.aiError = '';
    this.aiMessage = '';

    if (this.savingAiTasks) {
      return;
    }

    if (this.projects.length === 0) {
      this.aiError = 'Create a project before saving AI tasks.';
      return;
    }

    if (this.aiSuggestions.length === 0) {
      this.aiError = 'Generate at least one task before saving.';
      return;
    }

    const projectId = this.projects[0].id;
    this.savingAiTasks = true;

    const requests = this.aiSuggestions.map((suggestion) =>
      this.http.post<Task>(
        `${this.apiUrl}/projects/${projectId}/tasks`,
        {
          title: suggestion.title,
          description: suggestion.description,
          assignee: 'Roopesh',
          deadline: this.defaultAiDeadline(),
          status: 'TODO',
          priority: suggestion.priority
        }
      )
    );

    forkJoin(requests)
      .pipe(
        finalize(() => {
          this.savingAiTasks = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (createdTasks) => {
          this.aiMessage =
            `Successfully saved ${createdTasks.length} AI-generated tasks.`;
          this.aiSuggestions = [];
          this.loadDashboard();
        },
        error: (err) => {
          console.error('Save AI tasks error:', err);
          this.aiError =
            err.error?.message ??
            err.error?.detail ??
            'Could not save the AI tasks. Check the backend API.';
          this.cdr.detectChanges();
        }
      });
  }

  private defaultAiDeadline(): string {
    const deadline = new Date();
    deadline.setDate(deadline.getDate() + 7);

    const year = deadline.getFullYear();
    const month = String(deadline.getMonth() + 1).padStart(2, '0');
    const day = String(deadline.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }

  get completedCount(): number {
    return this.tasks.filter(task => task.status === 'DONE').length;
  }

  get todoCount(): number {
    return this.tasks.filter(task => task.status === 'TODO').length;
  }

  get inProgressCount(): number {
    return this.tasks.filter(
      task => task.status === 'IN_PROGRESS'
    ).length;
  }
}


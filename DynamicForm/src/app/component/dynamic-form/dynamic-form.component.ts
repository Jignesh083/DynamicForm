import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http'; 
import { FormService } from '../../services/dynamic-form-service.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dynamic-form',
  imports: [ReactiveFormsModule, CommonModule, FormsModule],
  templateUrl: './dynamic-form.component.html',
  styleUrls: ['./dynamic-form.component.css']
})
export class DynamicFormComponent implements OnInit {
  formFields: any[] = [
    { name: 'FirstName', type: 'text', selected: false },
    { name: 'LastName', type: 'text', selected: false },
    { name: 'Email', type: 'email', selected: false },
    { name: 'Phone', type: 'number', selected: false },
    { name: 'Address', type: 'text', selected: false },
    { name: 'Gender', type: 'radio', options: ['Male', 'Female'], selected: false },
  ];

  selectedFields: any[] = [];
  dynamicForm: FormGroup;
  formTitle: string = '';
  createdForms: any[] = [];
  qualificationCount: number = 0;
  educationDetailsSelected: boolean = false;

  constructor(
    private fb: FormBuilder,
    private formService: FormService,
    private http: HttpClient 
  ) {
    this.dynamicForm = this.fb.group({});
  }

  ngOnInit(): void {}

  onFieldSelect(field: any): void {
    if (this.selectedFields.includes(field)) {
      this.selectedFields = this.selectedFields.filter(f => f !== field);
      field.selected = false;
    } else {
      this.selectedFields.push(field);
      field.selected = true;
    }
  }

  onEducationDetailsChange(): void {
    if (this.educationDetailsSelected) {
      this.selectedFields.push({ name: 'Education', type: 'education' });
    } else {
      this.selectedFields = this.selectedFields.filter(field => field.name !== 'Education');
    }
  }

  addQualification(form: any): void {
    if (this.qualificationCount < 5) {
      this.qualificationCount++;
      const qualificationName = this.getQualificationName(this.qualificationCount);

      const newQualification = {
        qualificationLevel: qualificationName,
        schoolName: qualificationName + 'SchoolName',
        passingYear: qualificationName + 'PassingYear',
        percentage: qualificationName + 'Percentage',
        marksheet: qualificationName + 'Marksheet',
      };

      form.qualifications.push(newQualification);

      const newControls = {
        [newQualification.schoolName]: new FormControl(null, [Validators.required]),
        [newQualification.passingYear]: new FormControl(null, [Validators.required]),
        [newQualification.percentage]: new FormControl(null, [Validators.required]),
        [newQualification.marksheet]: new FormControl(null, [Validators.required]),
      };

      Object.keys(newControls).forEach(control => {
        form.dynamicForm.addControl(control, newControls[control]);
      });
    }
  }

  getQualificationName(count: number): string {
    switch (count) {
      case 1: return '10th';
      case 2: return '12th';
      case 3: return 'UG';
      case 4: return 'PG';
      case 5: return 'PhD';
      default: return `Qualification ${count}`;
    }
  }

  onCreateForm(): void {
      const newForm = {
      title: this.formTitle,
      selectedFields: [...this.selectedFields],
      dynamicForm: this.createDynamicForm(),
      qualifications: [],
    };

    this.createdForms.push(newForm);
    this.qualificationCount = 0;
    this.selectedFields = [];
    this.formTitle = '';
    this.formFields.forEach(field => {
      field.selected = false;
    });
  }

  createDynamicForm(): FormGroup {
    const controls: { [key: string]: FormControl } = {};

    this.selectedFields.forEach(field => {
      if (field.type === 'education') {
        this.createdForms.forEach(form => {
          form.qualifications.forEach((qualification: any) => {
            controls[qualification.schoolName] = new FormControl(null, [Validators.required]);
            controls[qualification.passingYear] = new FormControl(null, [Validators.required]);
            controls[qualification.percentage] = new FormControl(null, [Validators.required]);
            controls[qualification.marksheet] = new FormControl(null, [Validators.required]);
          });
        });
      } else {
        controls[field.name] = new FormControl(null, [Validators.required]);
      }
    });

    return this.fb.group(controls);
  }

  onSubmit(index: number): void {
    const form = this.createdForms[index];
    if (form.dynamicForm.valid) {
      const formData = new FormData();
      
      formData.append('formData', JSON.stringify({
        formTitle: form.title,
        fields: form.dynamicForm.value
      }));

      form.qualifications.forEach((qualification: any) => {
        formData.append('qualificationLevel', qualification.qualificationLevel); // Add qualification level
        if (qualification.marksheet) {
          formData.append('file', qualification.marksheet);
        }
      });

      this.http.post('http://localhost:8080/api/forms/submit', formData).subscribe(
        (response: any) => {
          if (response && response.message) {
            alert(response.message);
            form.dynamicForm.reset();
          }
        },
        (error) => {
          console.error('Error submitting form', error);
        }
      );
    }
  }

  closeForm(index: number): void {
    this.createdForms.splice(index, 1);
  }

  removeQualification(form: any, index: number): void {
    if (form.qualifications.length > 0) {
      const qualificationToRemove = form.qualifications[index];
      form.qualifications.splice(index, 1);
      Object.keys(qualificationToRemove).forEach(controlName => {
        form.dynamicForm.removeControl(controlName);  
        form.dynamicForm.controls[controlName].reset();  
      });
    }
  }

  // Handle file change for marksheets
  onFileChange(event: any, qualification: any): void {
    const file = event.target.files[0];  // Get the first file
    if (file) {
      qualification.marksheet = file;  // Store the file in the qualification object
    }
  }
}
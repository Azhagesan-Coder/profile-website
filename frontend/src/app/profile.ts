export interface Profile {
  name: string; role: string; location: string; email: string; phone: string;
  github: string; linkedin: string; summary: string;
  experience: {period: string; location: string; role: string; company: string; details: string[]}[];
  skills: {category: string; items: string[]}[];
  education: {degree: string; school: string; period: string; score: string}[];
  languages: string[];
}

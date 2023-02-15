import { IPerson } from 'app/shared/model/person.model';

export interface ILanguage {
  id?: number;
  person?: IPerson | null;
}

export const defaultValue: Readonly<ILanguage> = {};

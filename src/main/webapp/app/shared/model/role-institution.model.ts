import dayjs from 'dayjs';
import { IPerson } from 'app/shared/model/person.model';
import { IInstitution } from 'app/shared/model/institution.model';

export interface IRoleInstitution {
  id?: number;
  name?: string;
  start?: string | null;
  end?: string | null;
  people?: IPerson[] | null;
  institutions?: IInstitution[] | null;
}

export const defaultValue: Readonly<IRoleInstitution> = {};

import dayjs from 'dayjs';
import { IPerson } from 'app/shared/model/person.model';
import { IInstitution } from 'app/shared/model/institution.model';
import { IRoleWork } from 'app/shared/model/role-work.model';

export interface IWork {
  id?: number;
  name?: string | null;
  published?: string | null;
  people?: IPerson[] | null;
  institutions?: IInstitution[] | null;
  roleWorks?: IRoleWork[] | null;
}

export const defaultValue: Readonly<IWork> = {};

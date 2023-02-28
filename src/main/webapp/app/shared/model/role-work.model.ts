import dayjs from 'dayjs';
import { IPerson } from 'app/shared/model/person.model';
import { IWork } from 'app/shared/model/work.model';

export interface IRoleWork {
  id?: number;
  name?: string;
  start?: string | null;
  end?: string | null;
  people?: IPerson[] | null;
  works?: IWork[] | null;
}

export const defaultValue: Readonly<IRoleWork> = {};

import { ILocation } from 'app/shared/model/location.model';
import { IRoleInstitution } from 'app/shared/model/role-institution.model';
import { IWork } from 'app/shared/model/work.model';

export interface IInstitution {
  id?: number;
  name?: string | null;
  locations?: ILocation[] | null;
  roleInstitutions?: IRoleInstitution[] | null;
  works?: IWork[] | null;
}

export const defaultValue: Readonly<IInstitution> = {};

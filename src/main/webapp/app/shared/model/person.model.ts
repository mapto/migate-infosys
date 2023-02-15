import dayjs from 'dayjs';
import { ILanguage } from 'app/shared/model/language.model';
import { ICountry } from 'app/shared/model/country.model';
import { IRoleInstitution } from 'app/shared/model/role-institution.model';
import { IRoleWork } from 'app/shared/model/role-work.model';
import { IWork } from 'app/shared/model/work.model';

export interface IPerson {
  id?: number;
  name?: string;
  gender?: string | null;
  dob?: string | null;
  languages?: ILanguage[] | null;
  countries?: ICountry[] | null;
  roleInstitutions?: IRoleInstitution[] | null;
  roleWorks?: IRoleWork[] | null;
  works?: IWork[] | null;
}

export const defaultValue: Readonly<IPerson> = {};

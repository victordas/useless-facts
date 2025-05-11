import { UselessFact } from "./useless-fact.model";

export interface PaginatedResponse {
    items: UselessFact[]
    totalCount: number
    totalPages: number
}
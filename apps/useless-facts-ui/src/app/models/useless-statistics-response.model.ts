import { UselessFact } from "./useless-fact.model";

export interface UselessStatistics {
  totalFacts: number;
  factsAccessCount: { [key: string]: number };
  topAccessedFacts: UselessFact[];
  hitRate: number;
  missRate: number;
  requestCount: number;
  evictionCount: number;
}

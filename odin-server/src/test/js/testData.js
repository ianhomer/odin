import { MockFlux } from './flux/mockFlux.js'
import { Schema } from 'odin/schema/schema'

export const mockFlux = new MockFlux()
export const testSchema = new Schema({}, mockFlux)
export const testProject = {}
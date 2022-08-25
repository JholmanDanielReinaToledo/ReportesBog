import { Client } from 'pg'

export const clientPG = new Client(process.env.CONNECTION_STRING)

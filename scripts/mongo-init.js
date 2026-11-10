// Execute também em volumes existentes antes de iniciar a segunda entrega.
const base = db.getSiblingDB("medicare");
base.usuarios.createIndex({email: 1}, {unique: true});
base.medicamentos.createIndex({usuarioId: 1});

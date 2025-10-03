from dataclasses import dataclass

@dataclass(frozen=True)
class Vector:
    x: float
    y: float
    z: float

    def tuple(self) -> tuple[float, float, float]:
        return (self.x, self.y, self.z)

    def __add__(self, other: 'Vector') -> 'Vector':
        return Vector(self.x + other.x, self.y + other.y, self.z + other.z)

    def __sub__(self, other: 'Vector') -> 'Vector':
        return Vector(self.x - other.x, self.y - other.y, self.z - other.z)

    def __mul__(self, other: float) -> 'Vector':
        return Vector(self.x * other, self.y * other, self.z * other)

    def __rmul__(self, other: float) -> 'Vector':
        return self * other

    def __truediv__(self, other: float) -> 'Vector':
        return Vector(self.x / other, self.y / other, self.z / other)

    def dot(self, other: 'Vector') -> float:
        return self.x * other.x + self.y * other.y + self.z * other.z

    def norm2(self) -> float:
        return self.dot(self)

    def norm(self) -> float:
        return self.norm2() ** 0.5

    def __eq__(self, other: object) -> bool:
        if not isinstance(other, Vector):
            return NotImplemented
        return self.x == other.x and self.y == other.y and self.z == other.z

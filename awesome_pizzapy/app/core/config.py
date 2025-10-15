from __future__ import annotations

from functools import lru_cache
from typing import Optional

from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """Application configuration loaded from environment variables."""

    model_config = SettingsConfigDict(env_file=".env", env_prefix="", extra="ignore")

    app_name: str = Field(default="AwesomePizzaPy")
    database_url: Optional[str] = Field(default=None, alias="DATABASE_URL")

    spring_datasource_url: Optional[str] = Field(default=None, alias="SPRING_DATASOURCE_URL")
    spring_datasource_username: Optional[str] = Field(default=None, alias="SPRING_DATASOURCE_USERNAME")
    spring_datasource_password: Optional[str] = Field(default=None, alias="SPRING_DATASOURCE_PASSWORD")

    docs_url: Optional[str] = Field(default="/docs")
    redoc_url: Optional[str] = Field(default="/redoc")
    openapi_url: Optional[str] = Field(default="/openapi.json")

    default_database_url: str = "postgresql+psycopg://postgres:postgres@localhost:5432/awesomepizza"

    def resolved_database_url(self) -> str:
        """Resolve the SQLAlchemy database URL, honoring Spring-style variables."""

        if self.database_url:
            return self.database_url

        if self.spring_datasource_url:
            url = self.spring_datasource_url
            if url.startswith("jdbc:"):
                url = url[len("jdbc:") :]

            username = self.spring_datasource_username or ""
            password = self.spring_datasource_password or ""
            if username and password and "@" not in url.split("//", maxsplit=1)[-1]:
                scheme, rest = url.split("//", maxsplit=1)
                url = f"{scheme}//{username}:{password}@{rest}"

            if not url.startswith("postgresql+"):
                url = url.replace("postgresql://", "postgresql+psycopg://", 1)

            return url

        return self.default_database_url


@lru_cache()
def get_settings() -> Settings:
    return Settings()


settings = get_settings()
